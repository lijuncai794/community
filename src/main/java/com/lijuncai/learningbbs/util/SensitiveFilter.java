package com.lijuncai.learningbbs.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: 敏感词过滤器
 * @author: lijuncai
 **/
@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    //敏感词替换符
    private static final String REPLACEMENT = "***";

    //根节点
    private TrieNode rootNode = new TrieNode();

    /**
     * 初始化方法,加载敏感词文件
     *
     * @PostConstruct注解会使容器创建Bean的时候调用此方法进行初始化
     */
    @PostConstruct
    public void init() {
        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ) {
            String keyword;
            while ((keyword = reader.readLine()) != null) {
                // 添加到前缀树
                this.addKeyword(keyword);
            }
        } catch (IOException e) {
            logger.error("加载敏感词文件失败: " + e.getMessage());
        }
    }

    /**
     * 将一个敏感词添加到前缀树中
     *
     * @param keyword String 敏感词
     */
    private void addKeyword(String keyword) {
        TrieNode tempNode = rootNode;
        for (int i = 0; i < keyword.length(); i++) {
            char c = keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);

            //如果当前结点的子结点为空,则创建1个子节点并将字符设置进去
            if (subNode == null) {
                subNode = new TrieNode();
                tempNode.addSubNode(c, subNode);
            }

            //让当前结点的指针指向其子节点,进入下一轮循环
            tempNode = subNode;

            //设置结束标识
            if (i == keyword.length() - 1) {
                tempNode.setKeywordEnd(true);
            }
        }
    }

    /**
     * 过滤敏感词
     *
     * @param text String 待过滤的文本
     * @return String 过滤后的文本
     */
    public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }

        //指针1
        TrieNode tempNode = rootNode;
        //指针2
        int begin = 0;
        //指针3
        int position = 0;
        //过滤后的结果
        StringBuilder sb = new StringBuilder();

        while (position < text.length()) {
            char c = text.charAt(position);

            //跳过文本中的特殊符号
            if (isSymbol(c)) {
                //若指针1处于根节点,将此符号计入结果,让指针2向下走一步
                if (tempNode == rootNode) {
                    sb.append(c);
                    begin++;
                }
                //无论符号在开头或中间,指针3都向下走一步
                position++;
                continue;
            }

            //检查下级节点
            tempNode = tempNode.getSubNode(c);
            if (tempNode == null) {
                //以begin开头的字符串不是敏感词
                sb.append(text.charAt(begin));
                //进入下一个位置
                position = ++begin;
                //重新指向根节点
                tempNode = rootNode;
            } else if (tempNode.isKeywordEnd()) {
                //发现敏感词,将begin~position字符串替换掉
                sb.append(REPLACEMENT);
                //进入下一个位置
                begin = ++position;
                //重新指向根节点
                tempNode = rootNode;
            } else {
                //检查下一个字符
                position++;
            }
        }

        //将最后一批字符计入结果
        sb.append(text.substring(begin));
        return sb.toString();
    }

    /**
     * 判断当前字符是否为特殊符号
     *
     * @param c Character 字符
     * @return boolean 普通字符返回false，特殊字符返回true
     */
    private boolean isSymbol(Character c) {
        // 0x2E80~0x9FFF 是东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    //前缀树结点
    private class TrieNode {
        //标识该结点是否为一个敏感词的结尾
        private boolean isKeywordEnd = false;
        //子节点,Map中的key代表下级字符，value代表下级结点
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        //添加子节点
        public void addSubNode(Character c, TrieNode node) {
            subNodes.put(c, node);
        }

        //获取子节点
        public TrieNode getSubNode(Character c) {
            return subNodes.get(c);
        }
    }
}
