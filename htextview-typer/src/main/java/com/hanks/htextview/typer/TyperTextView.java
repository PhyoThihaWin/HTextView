package com.hanks.htextview.typer;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;

import com.hanks.htextview.base.AnimationListener;
import com.hanks.htextview.base.HTextView;

import java.util.Random;

/**
 * Typer Effect
 * Created by hanks on 2017/3/15.
 */

public class TyperTextView extends HTextView {

    public static final int INVALIDATE = 0x767;
    private Random random;
    private CharSequence mText;
    private Handler handler;
    private int charIncrease;
    private int typerSpeed;
    private CharSequence[] entries;
    private Boolean autoAnimate;
    private int index;

    private AnimationListener animationListener;

    public TyperTextView(Context context) {
        this(context, null);
    }

    public TyperTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TyperTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.TyperTextView);
        typerSpeed = typedArray.getInt(R.styleable.TyperTextView_typerSpeed, 100);
        charIncrease = typedArray.getInt(R.styleable.TyperTextView_charIncrease, 2);
        autoAnimate = typedArray.getBoolean(R.styleable.TyperTextView_autoAnimate, false);
        entries = typedArray.getTextArray(R.styleable.TyperTextView_android_entries);
        typedArray.recycle();

        random = new Random();
        mText = getText();
        handler = new Handler(msg -> {
            int currentLength = getText().length();
            if (currentLength < mText.length()) {
                if (currentLength + charIncrease > mText.length()) {
                    charIncrease = mText.length() - currentLength;
                }
                append(mText.subSequence(currentLength, currentLength + charIncrease));
                long randomTime = typerSpeed + random.nextInt(typerSpeed);
                Message message = Message.obtain();
                message.what = INVALIDATE;
                handler.sendMessageDelayed(message, randomTime);
                return false;
            } else {
                loopTextAnimation();
                if (animationListener != null) {
                    animationListener.onAnimationEnd(TyperTextView.this);
                }
            }
            return false;
        });
    }

    @Override
    public void setAnimationListener(AnimationListener listener) {
        animationListener = listener;
    }


    public int getTyperSpeed() {
        return typerSpeed;
    }

    public void setTyperSpeed(int typerSpeed) {
        this.typerSpeed = typerSpeed;
    }

    public int getCharIncrease() {
        return charIncrease;
    }

    public void setCharIncrease(int charIncrease) {
        this.charIncrease = charIncrease;
    }

    public void startTextAnimation() {
        loopTextAnimation();
    }

    @Override
    public void setProgress(float progress) {
        setText(mText.subSequence(0, (int) (mText.length() * progress)));
    }

    @Override
    public void animateText(CharSequence text) {
        if (text == null) {
            throw new RuntimeException("text must not be null");
        }

        mText = text;
        setText("");
        Message message = Message.obtain();
        message.what = INVALIDATE;
        handler.sendMessage(message);
    }

    private void loopTextAnimation() {
        // auto loop animation
        if (autoAnimate && entries != null && entries.length > 0) {
            if (index + 1 >= entries.length) {
                index = 0;
            }
            animateText(entries[index++]);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        handler.removeMessages(INVALIDATE);
    }
}
