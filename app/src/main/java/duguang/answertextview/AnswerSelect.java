package duguang.answertextview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * 第一种类型有备选词的完型填空
 */
public class AnswerSelect extends AppCompatActivity implements ReplaceSpan.OnClick,View.OnClickListener {

    private TextView mTv;
    private FlowLayout mFlow;

    private String mStr;
    private String[] mWords = {"mountain","rainy","Great Wall","wind","fine"};
    public DisplayMetrics mMetrics;
    private SpanController mSpc;//Span的控制类

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_answer_select);
        initView();
        initData();
    }

    private void initView() {
        mTv = (TextView) findViewById(R.id.mTv);
        mFlow = (FlowLayout) findViewById(R.id.mFlow);
    }

    private void initData() {
        mStr = getResources().getString(R.string.sentence);
        mSpc = new SpanController();
        mSpc.makeData(this,mTv,mStr);

        ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.rightMargin = px(10);
        for (int i = 0; i < mWords.length; i++) {
            addView(lp,mWords[i]);
        }
    }

    private void addView(ViewGroup.MarginLayoutParams lp, String str) {
        View v = View.inflate(this, R.layout.item_task_answer,null);
        TextView mTvKey = (TextView) v.findViewById(R.id.mTvKey);
        mTvKey.setText(str);
        mTvKey.setTag(str);
        mFlow.addView(v,lp);
    }

    @Override
    public void OnClick(TextView v, int id, ReplaceSpan span) {
        if (span.mObject == null)return;
        if (span.mObject instanceof View){
            View view = (View)span.mObject;
            setTvColor(view, true);
        }
        span.mText = "";
        span.mObject = null;
        mTv.invalidate();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
        case R.id.mTvKey:
            String key = (String) v.getTag();
            if (TextUtils.isEmpty(key)) return;
            int index = mSpc.setData(key, v);
            if (index != -1) {
                setTvColor(v, false);
            }
            break;
        }
    }

    private void setTvColor(View v, boolean enabled) {
        if (v instanceof  TextView){
            TextView tv = (TextView) v;
            tv.setTextColor(getResources().getColor(enabled?android.R.color.black:R.color.gray_e0e0e0));
        }
        v.setEnabled(enabled);
    }

    //dp转px
    public int px(float dp) {
        if (mMetrics == null){
            mMetrics = getResources().getDisplayMetrics();
        }
        int result = (int)(dp*mMetrics.density+0.5f);
        return result > 0 ? result : 1;
    }

}
