package duguang.answertextview;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ReplacementSpan;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * 自定义的Span,用来绘制填空题
 */
public class ReplaceSpan extends ReplacementSpan {

    public static interface OnClick{
        void OnClick(TextView v, int id, ReplaceSpan span);
    }

    public static interface OnSelect{
        void OnSelect(TextView v, Spannable buffer, int id, ReplaceSpan span);
    }

    public int id = 0;//回调中的对应Span的ID
    private int mWidth = 0;//最长单词的宽度
    public String mWidthStr;//对应句子最长的单词
    public String mText;//保存的String
    public Object mObject;//回调中的任意对象
    public OnClick mOnClick;
    public OnSelect mOnSelect;

    public void setWidth(String widthStr){
        mWidthStr = widthStr;
        mWidth = 0;
    }

    public void onClick(TextView v, Spannable buffer, boolean isDown, int x, int y, int line, int off){
        if (mOnClick != null){
            mOnClick.OnClick(v,id,this);
        }

        if (mOnSelect != null){
            mOnSelect.OnSelect(v,buffer,id,this);
        }
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        //将返回相对于Paint画笔的文本 50== 左右两边增加的空余长度
        if (mWidth == 0)mWidth = (int) paint.measureText(mWidthStr, 0, mWidthStr.length())+50;
        return mWidth;
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        //填入对应单词
        if (!TextUtils.isEmpty(mText)){
            int width = (int)paint.measureText(mText,0,mText.length());
            if (mWidth>width){
                width = (mWidth-width)/2;
            }else {
                width = 0;
            }
            canvas.drawText(mText,0,mText.length(),x+width,(float)y,paint);
        }
        //需要填写的单词下方画线
        //这里bottom-1，是为解决有时候下划线超出canvas
        canvas.drawLine(x, bottom -1, x + mWidth, bottom-1, paint);
    }

    //TextView触摸事件-->Span点击事件
    public static LinkMovementMethod Method = new LinkMovementMethod() {

        public boolean onTouchEvent(TextView widget, Spannable buffer,
                                    MotionEvent event) {
            int action = event.getAction();

            if (action == MotionEvent.ACTION_UP ||
                    action == MotionEvent.ACTION_DOWN) {
                int x = (int) event.getX();
                int y = (int) event.getY();

                x -= widget.getTotalPaddingLeft();
                y -= widget.getTotalPaddingTop();

                x += widget.getScrollX();
                y += widget.getScrollY();

                Layout layout = widget.getLayout();
                int line = layout.getLineForVertical(y);
                int off = layout.getOffsetForHorizontal(line, x);

                ReplaceSpan[] link = buffer.getSpans(off, off, ReplaceSpan.class);

                if (link.length != 0) {
                    //Span的点击事件
                    if (action == MotionEvent.ACTION_UP) {
                        link[0].onClick(widget,buffer,false,x,y,line,off);
                    } else if (action == MotionEvent.ACTION_DOWN) {
                        link[0].onClick(widget,buffer,true,x,y,line,off);
//                        Selection.setSelection(buffer,
//                                buffer.getSpanStart(link[0]),
//                                buffer.getSpanEnd(link[0]));
                    }
                    return true;
                } else {
//                    Selection.removeSelection(buffer);
                }
            }
            return false;
        }
    };

}
