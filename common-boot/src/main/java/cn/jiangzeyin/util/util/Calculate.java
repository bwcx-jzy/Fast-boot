package cn.jiangzeyin.util.util;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Stack;

/**
 * Created by jiangzeyin on 2017/6/14.
 */
public class Calculate {

    /**
     * 将字符串转化成List
     *
     * @param str str
     * @return array
     */
    public static ArrayList<String> getStringList(String str) {
        ArrayList<String> result = new ArrayList<>();
        StringBuilder num = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            if (Character.isDigit(str.charAt(i))) {
                num.append(str.charAt(i));
            } else {
                if (!Objects.equals(num.toString(), "")) {
                    result.add(num.toString());
                }
                result.add(str.charAt(i) + "");
                num = new StringBuilder();
            }
        }
        if (!Objects.equals(num.toString(), "")) {
            result.add(num.toString());
        }
        return result;
    }

    /**
     * 将中缀表达式转化为后缀表达式
     *
     * @param inOrderList list
     * @return array
     */
    public static ArrayList<String> getPostOrder(ArrayList<String> inOrderList) {
        ArrayList<String> result = new ArrayList<>();
        Stack<String> stack = new Stack<>();
        for (String anInOrderList : inOrderList) {
            if (Character.isDigit(anInOrderList.charAt(0))) {
                result.add(anInOrderList);
            } else {
                switch (anInOrderList.charAt(0)) {
                    case '(':
                        stack.push(anInOrderList);
                        break;
                    case ')':
                        while (!stack.peek().equals("(")) {
                            result.add(stack.pop());
                        }
                        stack.pop();
                        break;
                    default:
                        while (!stack.isEmpty() && compare(stack.peek(), anInOrderList)) {
                            result.add(stack.pop());
                        }
                        stack.push(anInOrderList);
                        break;
                }
            }
        }
        while (!stack.isEmpty()) {
            result.add(stack.pop());
        }
        return result;
    }

    /**
     * 计算后缀表达式
     *
     * @param postOrder post
     * @return int
     */
    public static Integer calculate(ArrayList<String> postOrder) {
        Stack<Integer> stack = new Stack<>();
        for (String aPostOrder : postOrder) {
            if (Character.isDigit(aPostOrder.charAt(0))) {
                stack.push(Integer.parseInt(aPostOrder));
            } else {
                Integer back = stack.pop();
                Integer front = stack.pop();
                Integer res = 0;
                switch (aPostOrder.charAt(0)) {
                    case '+':
                        res = front + back;
                        break;
                    case '-':
                        res = front - back;
                        break;
                    case '*':
                        res = front * back;
                        break;
                    case '/':
                        res = front / back;
                        break;
                }
                stack.push(res);
            }
        }
        return stack.pop();
    }

    /**
     * 比较运算符等级
     *
     * @param peek peek
     * @param cur  cur
     * @return boolean
     */
    public static boolean compare(String peek, String cur) {
        if ("*".equals(peek) && ("/".equals(cur) || "*".equals(cur) || "+".equals(cur) || "-".equals(cur))) {
            return true;
        } else if ("/".equals(peek) && ("/".equals(cur) || "*".equals(cur) || "+".equals(cur) || "-".equals(cur))) {
            return true;
        } else if ("+".equals(peek) && ("+".equals(cur) || "-".equals(cur))) {
            return true;
        } else if ("-".equals(peek) && ("+".equals(cur) || "-".equals(cur))) {
            return true;
        }
        return false;
    }

    public static int calculate(String s) {
        ArrayList result = getStringList(s);  //String转换为List
        result = getPostOrder(result);   //中缀变后缀
        return calculate(result);   //计算
    }

    public static void main(String[] args) {
        Calculate calculate = new Calculate();
        String s = "12+(23*3-56+7)*(2+90)/2";
        ArrayList result = calculate.getStringList(s);  //String转换为List
        result = calculate.getPostOrder(result);   //中缀变后缀
        int i = calculate.calculate(result);   //计算
        System.out.println(i);
    }
}