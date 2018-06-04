package com.zale.utils;

import com.blade.kit.StringKit;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by qyw on 2018/4/19.
 */
public class FilterBadWord {


    //敏感词文件,一行代表一个敏感词汇
    private static File wordfilter=null;

    //保存敏感词汇的文件(UTF-8),要求放在classPath根路径下
    public static final String fileName="CensorWords.txt";

    static {
        loadProps();

    }


    synchronized static private void loadProps(){

        try{
            wordfilter=new File(Thread.currentThread().getContextClassLoader().getResource(fileName).getPath());
        }catch (Exception e){
            System.out.println("读取文件失败!");
        }


    }


    private static long lastModified = 0L;

    private static List<String> words = new ArrayList<String>();

    private static void checkReload(){
        if(wordfilter.lastModified() > lastModified){
            synchronized(FilterBadWord.class){
                try{
                    lastModified = wordfilter.lastModified();
                    LineIterator lines = FileUtils.lineIterator(wordfilter, "utf-8");
                    while(lines.hasNext()){
                        String line = lines.nextLine();
                        if(StringKit.isNotBlank(line))
                            words.add((line).trim().toLowerCase());
                    }
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 检查字多个字符串中是否含有敏感词
     * @param contents
     */
    public static boolean checkContents(String ...contents) {
        if(!wordfilter.exists())
            return false;
        checkReload();

        for(String word : words){
            for(String content : contents)
                if(content!=null && content.indexOf(word) >= 0)
                    return true;
        }
        return false;
    }

    /**
     * 筛选多个字符串(字符串数组)中含有敏感词汇的set集合
     * @param contents
     */
    public static Set<String> pick(String ...contents) {
        if(!wordfilter.exists() || contents==null || contents.length==0)
            return Collections.EMPTY_SET;
        checkReload();
        Set<String> newWords=new HashSet<>();
        for(String word : words){
            for(String content : contents){
                if(content!=null && content.indexOf(word) >= 0)
                    newWords.add(word);
            }

        }
        return newWords;
    }



    /**
     * 检查字符串是否包含敏感词
     *
     * @param content
     * @return
     */
    public static boolean isContain(String content) {
        if(!wordfilter.exists())
            return false;
        checkReload();
        for(String word : words){
            if(content!=null && content.indexOf(word) >= 0)
                return true;
        }
        return false;
    }

    /**
     * 替换掉字符串中的敏感词
     *
     * @param str 等待替换的字符串,null替换为""
     * @param replaceChar 替换字符
     * @return
     */
    public static String replace(String str,String replaceChar){
        if(StringKit.isBlank(str)){
            return "";
        }
        checkReload();
        for(String word : words){
            if(str.indexOf(word)>=0){
                String reChar = "";
                for(int i=0;i<word.length();i++){
                    reChar += replaceChar;
                }
                str = str.replaceAll(word, reChar);
            }
        }
        return str;
    }

    public static List<String> lists() {
        checkReload();
        return words;
    }

    /**
     * 添加敏感词
     *
     * @param word
     * @throws IOException
     */
    public static void add(String word) throws IOException {
        word = word.toLowerCase();
        if(!words.contains(word)){
            words.add(word);
            FileUtils.writeLines(wordfilter, "UTF-8", words);
            lastModified = wordfilter.lastModified();
        }
    }

    /**
     * 删除敏感词
     *
     * @param word
     * @throws IOException
     */
    public static void delete(String word) throws IOException {
        word = word.toLowerCase();
        words.remove(word);
        FileUtils.writeLines(wordfilter, "UTF-8", words);
        lastModified = wordfilter.lastModified();
    }

    public static void main(String[] args) throws Exception{
        System.out.println(FilterBadWord.replace("中国共产党钓鱼岛sb","*"));
        System.out.println(FilterBadWord.replace(null,"*"));
        System.out.println(FilterBadWord.isContain("傻逼"));
        System.out.println(FilterBadWord.isContain("sb"));
        System.out.println(FilterBadWord.isContain("弱智"));
        System.out.println(FilterBadWord.pick("中国共产党钓鱼岛sb","sb12"));
        FilterBadWord.add("傻逼");
        System.out.println(FilterBadWord.lists());
    }


}
