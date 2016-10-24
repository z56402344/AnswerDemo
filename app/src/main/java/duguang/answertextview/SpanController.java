package duguang.answertextview;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 填空题的控制器
 */
public class SpanController {

    private TextView mTv;
    private SpannableString mSpanString;

    private int mFontT; // 字体top
    private int mFontB;// 字体bottom
    public int mOldSpan = -1;
    private String mStr;
    public String mWidthStr;
    private ArrayList<Integer> mListIndex = new ArrayList<Integer>();
    private ArrayList<ReplaceSpan> mSpans = new ArrayList<ReplaceSpan>();
    protected ImmFocus mFocus = new ImmFocus();

    private RectF mRf;

    //造对应的sentence
    public void makeData(Activity ac, TextView tv, String str){
        if (tv == null || TextUtils.isEmpty(str))return;
        try{
            tv.setMovementMethod(ReplaceSpan.Method);
            mTv = tv;
            char[] chars = str.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                //正常情况下要去掉'[',']',减掉mListIndex.size()
                if ('['==chars[i]){
                    mListIndex.add(i-mListIndex.size());
                }else if(']'==chars[i]){
                    mListIndex.add(i-mListIndex.size());
                }
            }
            mStr = str.replace("[","").replace("]","");
            String[] split = mStr.split(" ");
            int len =0;
            for (int i = 0; i < split.length; i++) {
                if (split[i].length() > len){
                    len = split[i].length();
                    mWidthStr = split[i];
                }
            }
            mSpanString = new SpannableString(mStr);
            int index = 0;
            for (int i = 0; i < mListIndex.size(); i+=2) {
                ReplaceSpan span = new ReplaceSpan();
                span.mOnClick = (ReplaceSpan.OnClick) ac;
                span.setWidth(mWidthStr);
                span.mText = "";
                span.id = index++;
                mSpans.add(span);
                mSpanString.setSpan(span, mListIndex.get(i), mListIndex.get(i+1), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        tv.setText(mSpanString);
    }

    //填充缓存的数据
    public void setData(String str, Object o, int i){
        if (mTv == null || mSpans ==null ||mSpans.size() ==0 || i<0 ||i>mSpans.size()-1)return;
        ReplaceSpan span = mSpans.get(i);
        span.mText = str;
        span.mObject = o;
        mTv.invalidate();
    }

    public int setData(String str, Object o){
        if (mTv == null)return -2;
        for (int i = 0; i < mSpans.size(); i++) {
            ReplaceSpan span = mSpans.get(i);
            if (TextUtils.isEmpty(span.mText)){
                span.mText = str;
                span.id = i;
                span.mObject = o;
                mTv.invalidate();
                return i;
            }
        }
        //-1说明填空题已经填满
        return -1;
    }

    public int isFill(){
        for (int i = 0; i < mSpans.size(); i++) {
            ReplaceSpan span = mSpans.get(i);
            if (TextUtils.isEmpty(span.mText)){
                return i;
            }
        }
        //-1说明填空题已经填满
        return -1;
    }

    //获取出对应Span的RectF数据
    public RectF drawSpanRect(TextView v, ReplaceSpan s) {
        Layout layout = v.getLayout();
        Spannable buffer = (Spannable) v.getText();
        int l = buffer.getSpanStart(s);
        int r = buffer.getSpanEnd(s);
        int line = layout.getLineForOffset(l);
        int l2 = layout.getLineForOffset(r);
        if (mRf == null){
            mRf = new RectF();
            Rect rt = new Rect();
            v.getPaint().getTextBounds("TgQyYjJ",0,7,rt);
            mFontT = rt.top;
            mFontB  = rt.bottom;
        }
        mRf.left = layout.getPrimaryHorizontal(l);
        mRf.right = layout.getSecondaryHorizontal(r);
        // 通过基线去校准
        line = layout.getLineBaseline(line);
        mRf.top = line + mFontT;
        mRf.bottom = line + mFontB;
        return mRf;
    }

    //设置EditText填空题中的相对位置
    public void setEtXY(TextView tv, EditText et, RectF rf) {
        //设置et w,h的值
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) et.getLayoutParams();
        lp.width = (int)(rf.right - rf.left);
        lp.height = (int)(rf.bottom - rf.top);
        //设置et 相对于tv x,y的相对位置
        lp.leftMargin = (int) (tv.getLeft()+rf.left);
        lp.topMargin  = (int) (tv.getTop()+rf.top);
        et.setLayoutParams(lp);
        //获取焦点，弹出软键盘
        et.setFocusable(true);
        et.requestFocus();
        showImm(true,et);
    }


    public String getAllAnswer(){
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < mSpans.size(); i++) {
            ReplaceSpan span = mSpans.get(i);
           if(i == mSpans.size() -1){
                sb.append(span.mText);
            }else{
                sb.append(span.mText+",");
            }
        }
        return sb.toString();
    }

    public ArrayList<ReplaceSpan> getSpanAll(){
        return mSpans;
    }

    public void showImm(boolean bOn,View focus) {
        try {
            if (bOn) {
                if (focus!=null) {
                    ImmFocus.show(true, focus);
                } else {
                    mFocus.setFocus(focus);
                }
            } else {
                ImmFocus.show(false, null);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
