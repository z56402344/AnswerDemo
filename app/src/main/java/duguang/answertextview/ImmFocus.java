package duguang.answertextview;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

// 处理焦点
public class ImmFocus {
	// 处理编辑框焦点及输入法
	protected View mLastFocus;
	// 保存焦点
	public void save(View focus) {
		mLastFocus = focus;
		if (mLastFocus!=null&&(!show(false,mLastFocus)||!(mLastFocus instanceof TextView))) mLastFocus = null;
	}
	// 恢复焦点
	public void restore() {
		if (mLastFocus!=null) {
			show(true,mLastFocus);
			mLastFocus = null;
		}
	}
	// 预约焦点
	public void setFocus(View focus) {
		mLastFocus = focus;
	}
	// 显示/隐藏
	public static boolean show(boolean bOn,View focus) {
		InputMethodManager imm = (InputMethodManager)focus.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		if (bOn) {
			focus.requestFocus();
			return imm.showSoftInput(focus, 0);
		} else {
			return imm.hideSoftInputFromWindow(focus.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
}
