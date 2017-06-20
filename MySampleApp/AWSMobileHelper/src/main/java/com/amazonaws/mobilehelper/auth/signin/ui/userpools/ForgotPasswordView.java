package com.amazonaws.mobilehelper.auth.signin.ui.userpools;


import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.amazonaws.mobilehelper.R;
import com.amazonaws.mobilehelper.auth.signin.SignInManager;
import com.amazonaws.mobilehelper.auth.signin.ui.DisplayUtils;
import com.amazonaws.mobilehelper.auth.signin.ui.SplitBackgroundDrawable;
import com.amazonaws.mobilehelper.config.AWSMobileHelperConfiguration;

import static com.amazonaws.mobilehelper.auth.signin.ui.userpools.UserPoolFormConstants.FORM_BUTTON_COLOR;
import static com.amazonaws.mobilehelper.auth.signin.ui.userpools.UserPoolFormConstants.FORM_BUTTON_CORNER_RADIUS;
import static com.amazonaws.mobilehelper.auth.signin.ui.userpools.UserPoolFormConstants.FORM_SIDE_MARGIN_RATIO;
import static com.amazonaws.mobilehelper.auth.signin.ui.userpools.UserPoolFormConstants.MAX_FORM_WIDTH_IN_PIXELS;

public class ForgotPasswordView extends LinearLayout {
    private FormView forgotPassForm;
    private EditText verificationCodeEditText;
    private EditText passwordEditText;

    private SplitBackgroundDrawable splitBackgroundDrawable;

    public ForgotPasswordView(final Context context) {
        this(context, null);
    }

    public ForgotPasswordView(final Context context, @Nullable final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ForgotPasswordView(final Context context, @Nullable final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);

        final int backgroundColor;
        if (isInEditMode()) {
            backgroundColor = Color.DKGRAY;
        } else {
            final AWSMobileHelperConfiguration helperConfig =
                SignInManager.getInstance().getIdentityManager().getHelperConfiguration();
            backgroundColor = helperConfig.getSignInBackgroundColor(Color.DKGRAY);
        }
        splitBackgroundDrawable = new SplitBackgroundDrawable(0, backgroundColor);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        forgotPassForm = (FormView) findViewById(R.id.forgot_password_form);

        verificationCodeEditText = forgotPassForm.addFormField(getContext(),
            InputType.TYPE_CLASS_NUMBER,
            getContext().getString(R.string.sign_up_confirm_code));

        passwordEditText = forgotPassForm.addFormField(getContext(),
            InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD,
            getContext().getString(R.string.sign_in_password));

        setupConfirmButtonColor();
    }

    private void setupConfirmButtonColor() {
        final Button confirmButton = (Button) findViewById(R.id.forgot_password_button);
        confirmButton.setBackgroundDrawable(DisplayUtils.getRoundedRectangleBackground(
            FORM_BUTTON_CORNER_RADIUS, FORM_BUTTON_COLOR));
        LayoutParams signUpButtonLayoutParams = (LayoutParams) confirmButton.getLayoutParams();
        signUpButtonLayoutParams.setMargins(
            forgotPassForm.getFormShadowMargin(),
            signUpButtonLayoutParams.topMargin,
            forgotPassForm.getFormShadowMargin(),
            signUpButtonLayoutParams.bottomMargin);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        final int maxWidth = Math.min((int)(parentWidth * FORM_SIDE_MARGIN_RATIO), MAX_FORM_WIDTH_IN_PIXELS);
        super.onMeasure(MeasureSpec.makeMeasureSpec(maxWidth, MeasureSpec.AT_MOST), heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        setupSplitBackground();
    }

    private void setupSplitBackground() {
        splitBackgroundDrawable.setSplitPointDistanceFromTop(forgotPassForm.getTop()
            + (forgotPassForm.getMeasuredHeight()/2));
        ((ViewGroup)getParent()).setBackgroundDrawable(splitBackgroundDrawable);
    }

    public String getVerificationCode() {
        return verificationCodeEditText.getText().toString();
    }

    public String getPassword() {
        return passwordEditText.getText().toString();
    }
}
