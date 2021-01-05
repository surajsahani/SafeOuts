package martialcoder.surajsahani.safeouts.zomatodata;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;


public class CanaroTextView extends AppCompatTextView {
    public CanaroTextView(Context context) {
        super(context);
        init();
    }
    public CanaroTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CanaroTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
         init();
    }
    private void init() {
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "canaro_extra_bold.otf");
        setTypeface(typeface);
    }

}
