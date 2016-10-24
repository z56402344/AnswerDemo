package duguang.answertextview;

import android.app.Activity;
import android.graphics.RectF;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * 第二种类型有备选词的完型填空.
 */
public class AnswerEditText extends Activity implements ReplaceSpan.OnClick, View.OnClickListener{

    private TextView mTv;
    private EditText mEt;

    private String mStr;
    private SpanController mSpc;//Span的控制类

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_answer_edit);
        initView();
        initData();
    }

    private void initView() {
        mTv = (TextView) findViewById(R.id.mTv);
        mEt = (EditText) findViewById(R.id.mEt);
    }

    private void initData() {
        mStr = getResources().getString(R.string.sentence);
        mSpc = new SpanController();
        mSpc.makeData(this,mTv,mStr);

        mEt.addTextChangedListener(mWatcher);
        mEt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mSpc.mWidthStr.length())});
    }

    @Override
    public void OnClick(TextView tv, int id, ReplaceSpan span) {
        mSpc.setData(mEt.getText().toString(),null,mSpc.mOldSpan);
        mSpc.mOldSpan = id;
        //如果当前span身上有值，先赋值给et身上
        mEt.setText(TextUtils.isEmpty(span.mText)?"":span.mText);
        //通过rf计算出et当前应该显示的位置
        RectF rf = mSpc.drawSpanRect(tv,span);
        //设置EditText填空题中的相对位置
        mSpc.setEtXY(tv,mEt,rf);
    }

    @Override
    public void onClick(View v) {
    }

    //输入填空的监听
    private TextWatcher mWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            try{
                mSpc.setData(s.toString(),null,mSpc.mOldSpan);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };
}
