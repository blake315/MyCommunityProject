package com.zeeway.community.util;


import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Thales
 * 敏感词过滤器，其中TrieNode是内部类，用来构造敏感词前缀树
 * 其中，前缀树的具体实现使用hashmap实现，敏感字符为key，下级节点为value
 */
@Component
public class SensitiveFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SensitiveFilter.class);

    //用于替换敏感字符的字符
    private static final String REPLACE = "****";

    private TrieNode rootNode = new TrieNode();

    @PostConstruct
    public void init(){
        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                final BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        ){
            String keyWord;
            while ((keyWord = reader.readLine()) != null){
                this.addKeyWord(keyWord);
            }
        } catch (Exception e) {
            LOGGER.error("sensitive words file loading failed..." + e.getMessage());
        }

    }



    private void addKeyWord(String keyWord){
        TrieNode temp = rootNode;
        for (int i = 0 ; i < keyWord.length() ; i++){
            char c = keyWord.charAt(i);
            TrieNode node1 = temp.getSubNode(c);
            if (node1 == null){
                node1 = new TrieNode();
                temp.addSubNode(c, node1);
            }

            temp = node1;

            if (i == keyWord.length()-1){
                temp.setKeyWordEnd(true);
            }
        }
    }


    public String Filter(String text){
        if (StringUtils.isBlank(text)){
            return null;
        }

        TrieNode tempNode = rootNode;
        int begin = 0;
        int position = 0;
        StringBuilder stringBuilder = new StringBuilder();
        while (position < text.length()){
            char c = text.charAt(position);
            if (isSymbol(c)){
                if (tempNode == rootNode){
                    stringBuilder.append(c);
                    begin++;
                }
                position++;
                if (position >= text.length() - 1){
                    if (begin >= text.length()){
                        break;
                    }
                    stringBuilder.append(text.charAt(begin));
                    begin++;
                    position = begin;
                    tempNode = rootNode;
                }
                continue;
            }

            tempNode  = tempNode.getSubNode(c);
            if(tempNode == null){
                stringBuilder.append(text.charAt(begin));
                position = ++begin;
                tempNode = rootNode;
            }else if (tempNode.isKeyWordEnd()){
                stringBuilder.append(REPLACE);
                position ++;
                begin = position;
                tempNode = rootNode;
            }else if (position >= text.length() - 1){
                stringBuilder.append(text.charAt(begin));
                begin++;
                position = begin;
                tempNode = rootNode;
            }else{
                if (position < text.length() - 1){
                    position ++;
                }
            }
        }

        stringBuilder.append(text.substring(begin));
        return stringBuilder.toString();


    }


    private boolean isSymbol(Character character) {
        return !CharUtils.isAsciiAlphanumeric(character) && (character < 0x2E80 || character > 0x9FFF);
    }


    private class TrieNode{
        private boolean isKeyWordEnd = false;

        //子节点
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        public boolean isKeyWordEnd() {
            return isKeyWordEnd;
        }

        public void setKeyWordEnd(boolean keyWordEnd) {
            isKeyWordEnd = keyWordEnd;
        }

        public void addSubNode(Character c, TrieNode node){
            subNodes.put(c,node);
        }

        public TrieNode getSubNode(Character c){
            return subNodes.get(c);

        }

    }
}
