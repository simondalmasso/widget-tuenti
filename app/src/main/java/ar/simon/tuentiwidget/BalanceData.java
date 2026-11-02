package ar.simon.tuentiwidget;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class BalanceData {
    final long remainingBytes, initialBytes, updatedAt;
    final String expirationDate, title;
    BalanceData(long remaining, long initial, String expiry, String title) {
        this(remaining, initial, expiry, title, System.currentTimeMillis());
    }
    BalanceData(long remaining, long initial, String expiry, String title, long updated) {
        remainingBytes=remaining; initialBytes=initial; expirationDate=expiry==null?"":expiry;
        this.title=title==null?"":title; updatedAt=updated;
    }
    long usedBytes() { return initialBytes>0 && remainingBytes>=0 ? Math.max(0, initialBytes-remainingBytes) : 0; }
    int usedPercent() { return initialBytes>0 && remainingBytes>=0 ? (int)Math.max(0,Math.min(100,Math.round(100d*usedBytes()/initialBytes))) : 0; }
    String remainingGb() { return formatGb(remainingBytes); }
    static String formatGb(long bytes) {
        if(bytes<0) return "∞";
        return String.format(Locale.US,"%.1f",bytes/(1024d*1024d*1024d));
    }
    static BalanceData fromPageText(String text) {
        if(text==null) return null;
        String[] lines=text.replace('\u00a0',' ').replace("\r","\n").split("\n");
        int start=-1;
        for(int i=0;i<lines.length;i++) if(lines[i].toLowerCase(Locale.ROOT).contains("datos disponibles")) { start=i; break; }
        if(start<0) return null;
        StringBuilder block=new StringBuilder();
        for(int i=start;i<Math.min(lines.length,start+10);i++) {
            String l=lines[i].trim();
            if(i>start && (l.toLowerCase(Locale.ROOT).contains("internet por día") || l.toLowerCase(Locale.ROOT).contains("internet por dia"))) break;
            block.append(l).append('\n');
        }
        Matcher m=Pattern.compile("(?i)(ilimitado|[0-9]+(?:[.,][0-9]+)?\\s*(?:GB|MB|KB))").matcher(block);
        if(!m.find()) return null;
        long remaining=parse(m.group());
        if(!m.find()) return null;
        long total=parse(m.group());
        if(total<=0 && remaining!=-1) return null;
        Matcher d=Pattern.compile("\\b[0-9]{1,2}/[0-9]{1,2}/[0-9]{2,4}\\b").matcher(block);
        return new BalanceData(remaining,total,d.find()?d.group():"","Datos disponibles");
    }
    private static long parse(String s) {
        if(s.equalsIgnoreCase("ilimitado")) return -1;
        Matcher m=Pattern.compile("(?i)([0-9]+(?:[.,][0-9]+)?)\\s*(GB|MB|KB)").matcher(s);
        if(!m.find()) return 0;
        double n=Double.parseDouble(m.group(1).replace(',','.'));
        int p=m.group(2).equalsIgnoreCase("GB")?3:m.group(2).equalsIgnoreCase("MB")?2:1;
        return Math.round(n*Math.pow(1024,p));
    }
}
