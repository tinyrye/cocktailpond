package com.tinook.common.text;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * All event handling methods are stubbed; an actual watcher may (and likely) will only
 * care to implement <code>afterTextChanged(Editable)</code>.
 */
public class AbstractTextWatcher implements TextWatcher
{
    @Override
    public void afterTextChanged(final Editable s) {

    }
    
    @Override
    public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {

    }
    
    @Override
    public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {

    }
}