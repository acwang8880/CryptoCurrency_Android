package com.example.alex.crytoconv;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

/**
 * Created by Alex on 1/28/2018.
 */

public class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Toast.makeText(adapterView.getContext(),
                "Selected: " + adapterView.getItemAtPosition(i).toString(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // Do Nothing
    }
}
