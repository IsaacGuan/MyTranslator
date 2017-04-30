package com.gyr.translator.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

public class ClearableEditText extends EditText {

	private Drawable drawableRight;
	private boolean showClearIcon;

	public ClearableEditText(Context context) {
		super(context);
	}

	public ClearableEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ClearableEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		Drawable[] drawables = getCompoundDrawables();
		// Store drawableRight
		drawableRight = drawables[2];
		setCompoundDrawablesWithIntrinsicBounds(drawables[0], drawables[1],
				null, drawables[3]);
	}
	
	@Override
	protected void onTextChanged(CharSequence text, int start,
			int lengthBefore, int lengthAfter) {
		super.onTextChanged(text, start, lengthBefore, lengthAfter);

		if (0 == text.length()) {
			showClearIcon = false;
		} else {
			showClearIcon = true;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP
				&& showClearIcon
				&& drawableRight != null
				&& event.getX() >= (getWidth() - drawableRight.getIntrinsicWidth()
						- getPaddingRight())
				&& event.getY() <= (getPaddingTop() + drawableRight.getIntrinsicHeight())) {

			setText("");

			int cacheInputType = getInputType();
			setInputType(InputType.TYPE_NULL);
			super.onTouchEvent(event);
			setInputType(cacheInputType);
			return true;
		}
		return super.onTouchEvent(event);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (showClearIcon && drawableRight != null) {
			float dy = getPaddingTop();
			float dx = getWidth() - drawableRight.getIntrinsicWidth()
					- getPaddingRight();
			canvas.save();
			canvas.translate(dx, dy);
			drawableRight.draw(canvas);
			canvas.restore();
		}
		super.onDraw(canvas);
	}

}