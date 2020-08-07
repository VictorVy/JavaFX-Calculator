package sample;

//import javafx.event.Event;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
//import javafx.scene.input.KeyEvent;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;

public class Controller
{
    final Clipboard clipboard = Clipboard.getSystemClipboard();
    final ClipboardContent content = new ClipboardContent();

    //injecting controls
    public Label display;

    public Button b0, b1, b2, b3, b4, b5, b6, b7, b8, b9;
    public Button bDot, bDel, bClear, bAns, bPi;
    public Button bDiv, bTimes, bSub, bAdd, bSqrt, bCbrt, bSqr, bPow, bFact, bEquals;

    public ToggleButton b2nd;

    //injecting methods
    public void b0() { buttonClick("0"); }
    public void b1() { buttonClick("1"); }
    public void b2() { buttonClick("2"); }
    public void b3() { buttonClick("3"); }
    public void b4() { buttonClick("4"); }
    public void b5() { buttonClick("5"); }
    public void b6() { buttonClick("6"); }
    public void b7() { buttonClick("7"); }
    public void b8() { buttonClick("8"); }
    public void b9() { buttonClick("9"); }

    public void bDot() { buttonClick("."); }
    public void bDel() { buttonClick("del"); }
    public void bClear() { buttonClick("c"); }
    public void bAns() { buttonClick("ans"); }
    public void bPi() { buttonClick("pi"); }

    public void bDiv() { buttonClick("/"); }
    public void bTimes() { buttonClick("*"); }
    public void bSub() { buttonClick("-"); }
    public void bAdd() { buttonClick("+"); }
    public void bSqrt() { buttonClick("sqrt"); }
    public void bCbrt() { buttonClick("cbrt"); }
    public void bSqr() { buttonClick("sqr"); }
    public void bPow() { buttonClick("^"); }
    public void bFact() { buttonClick("fact"); }
    public void bEquals() { buttonClick("="); }

    public void b2nd()
    {
        bCbrt.setVisible(b2nd.isSelected());
        bPow.setVisible(b2nd.isSelected());

        bSqrt.setVisible(!b2nd.isSelected());
        bSqr.setVisible(!b2nd.isSelected());
    }

    public void displayClick()
    {
        content.putString(display.getText());
        clipboard.setContent(content);
        content.clear();
    }

//    public void keyPressed(KeyEvent key) //doesn't work
//    {
//        switch(key.getCode())
//        {
//            case PERIOD -> bDot();
//            case BACK_SPACE, DELETE -> bDel();
//            case C, ESCAPE -> bClear();
//            case SLASH, DIVIDE -> bDiv();
//            case STAR, ASTERISK, MULTIPLY -> bTimes();
//            case MINUS, SUBTRACT -> bSub();
//            case PLUS, ADD -> bAdd();
//            case EXCLAMATION_MARK -> bFact();
//            case EQUALS -> bEquals();
//            case DIGIT0 -> b0();
//            case DIGIT1 -> b1();
//            case DIGIT2 -> b2();
//            case DIGIT3 -> b3();
//            case DIGIT4 -> b4();
//            case DIGIT5 -> b5();
//            case DIGIT6 -> b6();
//            case DIGIT7 -> b7();
//            case DIGIT8 -> b8();
//            case DIGIT9 -> b9();
//        }
//    }

    boolean replaceAns = false;
    String answer;

    //redirecting button clicks
    private void buttonClick(String id) //I wish Java 8 supported enhanced switch statements!
    {
        switch(id)
        {
            case "0": case "1": case "2": case "3": case "4": case "5": case "6": case "7": case "8": case "9": case "ans": case "pi": mainButtons(id); break;
            case "-": sub(); break;
            case "+": case "*": case "/": case "^": opButtons(id); break;
            case "sqrt": case "cbrt": case "sqr": case "fact": inlineOps(id); break;
            case ".": dot(); break;
            case "del": delete(); break;
            case "c": clear(); break;
            case "=": solve(); break;
            default: replace("Unknown Error");
        }
    }

    //handling button events (terrible readability, sorry)
    private void mainButtons(String id)
    {
        if(replaceAns || getNum().equals("0"))
        {
            replace(display.getText().substring(0, display.getText().length() - getNum().length()) +
                    (id.equals("ans") ? answer : id.equals("pi") ? Double.toString(Math.PI) : id)); //I should really just use if-else here

            bDot.setDisable(id.equals("pi"));
            replaceAns = id.equals("ans") || id.equals("pi");
        }
        else if(id.equals("ans"))
            append(display.getText().endsWith("--") && Double.parseDouble(answer) < 0 ? answer.substring(1) : answer);
        else if(id.equals("pi"))
        {
            append(Double.toString(Math.PI));
            bDot.setDisable(true);
        }
        else
            append(id);

        disableOps(false);
        disableInlines(false);
        bSub.setDisable(false);
        bAns.setDisable(true);
        bPi.setDisable(true);
        bFact.setDisable(display.getText().contains(".") && !display.getText().contains("E"));
        bEquals.setDisable(!containsOp(display.getText().substring(1)));

        int opCount = 0;
        int dotCount = 0;
        for(int i = 0; i < display.getText().length(); i++)
        {
            if(isOp(display.getText().charAt(i))) opCount++;
            if(display.getText().charAt(i) == '.') dotCount++;
        }
        bDot.setDisable(dotCount > opCount);
    }

    private void sub()
    {
        if(display.getText().equals("0")) replace("-");
        else append("-");

        disableOps(true);
        disableInlines(true);
        bDot.setDisable(true);
        bFact.setDisable(true);
        bPi.setDisable(false);
        replaceAns = false;
        bSub.setDisable(display.getText().equals("-") || display.getText().endsWith("--"));
        bAns.setDisable(answer == null);
    }

    private void opButtons(String id)
    {
        append(id);

        disableOps(true);
        disableInlines(true);
        bDot.setDisable(true);
        bEquals.setDisable(true);
        bFact.setDisable(true);
        bPi.setDisable(false);
        replaceAns = false;
        bAns.setDisable(answer == null);
    }

    private void inlineOps(String id)
    {
        String result = "";

        switch(id)
        {
            case "sqrt": result = String.valueOf(Math.sqrt(Double.parseDouble(getNum()))); break;
            case "cbrt": result = String.valueOf(Math.cbrt(Double.parseDouble(getNum()))); break;
            case "sqr": result = getNum().contains(".") ?
                    new BigDecimal(getNum()).multiply(new BigDecimal(getNum())).toString() :
                    new BigInteger(getNum()).multiply(new BigInteger(getNum())).toString();
                    break;
            case "fact":
                try
                { result = fact(new BigInteger(getNum())).toString(); }
                catch(StackOverflowError e)
                {
                    replace("Too Big");

                    replaceAns = true;
                    disableInlines(true);
                    bFact.setDisable(true);
                    bSub.setDisable(true);
                    disableOps(true);

                    return;
                }
                break;
        }

        if(!containsOp(display.getText()))
        {
            answer = result;
            replaceAns = true;
        }
        if(result.endsWith(".0"))
            result = result.substring(0, result.length() - 2);

        bPi.setDisable(false);
        bDot.setDisable(result.contains("."));
        bFact.setDisable(result.contains(".") && !result.contains("E"));

        replace(display.getText().substring(0, display.getText().length() - getNum().length()) + result);
    }

    private void dot()
    {
        append(".");
        disableOps(true);
        disableInlines(true);
        bSub.setDisable(true);
        bDot.setDisable(true);
        bPi.setDisable(true);
        bFact.setDisable(true);
        replaceAns = false;
    }

    private void delete()
    {
        if(display.getText().equals("Too Big"))
        {
            clear();
            return;
        }
        else if(display.getText().length() <= 1)
            display.setText("0");
        else
        {
            display.setText(display.getText().substring(0, display.getText().length() - 1));

            if(endsOp(display.getText()))
            {
                disableOps(true);
                disableInlines(true);
                bDot.setDisable(true);
                bEquals.setDisable(true);
                bFact.setDisable(true);
            }
            else
            {
                disableOps(false);
                disableInlines(false);
                bSub.setDisable(false);
                bDot.setDisable(false);
                bPi.setDisable(true);

                bEquals.setDisable(!containsOp(display.getText()));
                bFact.setDisable(display.getText().contains("."));
            }
        }
    }

    private void clear()
    {
        replace("0");

        disableOps(false);
        disableInlines(false);
        bSub.setDisable(false);
        bDot.setDisable(false);
        bPi.setDisable(false);
        bFact.setDisable(false);
        bEquals.setDisable(true);
        bAns.setDisable(answer == null);
    }

    private void solve()
    {
        String input = display.getText();

        ArrayList<String> terms = new ArrayList<>();

        StringBuilder num = new StringBuilder();

        for(int i = 0; i < input.length(); i++) //puts all the terms of the expression into an arraylist
        {
            if(!isOp(input.charAt(i)))
            {
                num.append(input.charAt(i));

                if(i == input.length() - 1)
                    terms.add(num.toString());
            }
            else
            {
                if(!num.toString().equals(""))
                    terms.add(num.toString());
                num = new StringBuilder();

                if(input.charAt(i) == '-')
                {
                    num.append("-");
                    if(i != 0 && !isOp(input.charAt(i + 1)) && !isOp(input.charAt(i - 1)))
                        terms.add("+");
                }
                else
                    terms.add(Character.toString(input.charAt(i)));
            }
        }

        for(int i = 0; i < terms.size(); i++) //loops through the arraylist to solve the expression
        {
            if(terms.get(i).equals("*") || terms.get(i).equals("/")) //first pass, does multiplication and division
            {
                double result = calculate(terms.get(i), terms.get(i - 1), terms.get(i + 1));

                terms.remove(i - 1);
                terms.remove(i - 1);
                terms.remove(i - 1);

                terms.add(--i, Double.toString(result));

                if(!terms.subList(i, terms.size()).contains("*") && !terms.subList(i, terms.size()).contains("*"))
                    i = 0;
            }
            else if(isOp(terms.get(i))) //second pass, does addition and subtraction
            {
                double result = calculate(terms.get(i), terms.get(i - 1), terms.get(i + 1));

                terms.remove(i - 1);
                terms.remove(i - 1);
                terms.remove(i - 1);

                terms.add(--i, Double.toString(result));
            }
        }

        answer = terms.get(0);

        if(answer.endsWith(".0"))
            answer = answer.substring(0, answer.length() - 2);
        if(answer.equals("Infinity"))
            answer = "Too Big";
        if(answer.equals("NaN"))
            answer = "Undefined";

        replace(answer);

        disableInlines(false);
        replaceAns = true;
        bAns.setDisable(true);
        bEquals.setDisable(true);
        bPi.setDisable(false);
        bFact.setDisable(answer.contains(".") && !answer.contains("E"));
    }

    //helper methods
    private double calculate(String op, String a, String b) //where the math actually occurs
    {
        double result;
        double x = Double.parseDouble(a);
        double y = Double.parseDouble(b);

        switch(op)
        {
            case "+": result = x + y; break;
            case "-": result = x - y; break;
            case "*": result = x * y; break;
            case "/": result = x / y; break;
            case "^": result = Math.pow(x, y); break;
            default: result = -1;
        }

        return result;
    }

    private BigInteger fact(BigInteger num) //factorial method
    {
        if (num.compareTo(BigInteger.valueOf(2)) <= 0) return num;

        return fact(num.subtract(BigInteger.ONE)).multiply(num);
    }

    private String getNum() //returns the right-most term in the expression
    {
        String input = display.getText();
        String num = "";

        for(int i = input.length() - 1; i >= 0; i--)
        {
            if(!isOp(input.charAt(i)))
                num = input.charAt(i) + num;
            else
            {
                num = input.charAt(i) == '-' ?
                        i == 0 ? "-" + num : num :
                        input.charAt(i - 1) == '-' ? "-" + num : num;
                break;
            }
        }

        return num;
    }

    //useful booleans
    private boolean isOp(String s) { return s.equals("+") || s.equals("-") || s.equals("*") || s.equals("/") || s.equals("^"); }
    private boolean isOp(Character c) { return c == '+' || c == '-' || c == '*' || c == '/' || c == '^'; }
    private boolean containsOp(String s) { return s.contains("+") || s.contains("-") || s.contains("*") || s.contains("/") || s.contains("^"); }
    private boolean endsOp(String s) { return s.endsWith("+") ||  s.endsWith("-") || s.endsWith("*") || s.endsWith("/") || s.endsWith("^"); }

    //modifies displayed text
    private void append(String s) { display.setText(display.getText() + s); }
    private void replace(String s) { display.setText(s); }

    //disables groups of buttons
    private void disableOps(boolean b)
    {
        bAdd.setDisable(b);
        bTimes.setDisable(b);
        bDiv.setDisable(b);
        bPow.setDisable(b);
    }
    private void disableInlines(boolean b)
    {
        bSqrt.setDisable(b);
        bCbrt.setDisable(b);
        bSqr.setDisable(b);
    }
}
