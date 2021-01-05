package martialcoder.surajsahani.safeouts.zomatodata;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;


public class GothamTextView extends AppCompatTextView {
    public GothamTextView(Context context) {
        super(context);
        init();
    }

    public GothamTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GothamTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init()
    {
        Typeface typeface=Typeface.createFromAsset(getContext().getAssets(),"gotham.otf");
        setTypeface(typeface);
    }
}