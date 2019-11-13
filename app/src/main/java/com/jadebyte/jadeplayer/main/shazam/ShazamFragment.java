package com.jadebyte.jadeplayer.main.shazam;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.jadebyte.jadeplayer.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShazamFragment extends Fragment {


    private RecordView recordView;
    private boolean isHumming = false;

    public ShazamFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FrameLayout root = (FrameLayout) inflater.inflate(R.layout.dummy_fragment_layout, container, false);


        recordView = new RecordView(getActivity(), (currentFile) -> {
            ResultTrack resultTrack = API.getInstance().recognizeVoice(currentFile, isHumming);
        });
        root.addView(recordView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER));



        HummingSupplier singingSupplier = () -> isHumming;

        final ToggleIcon toggleIcon = new ToggleIcon(getContext(), singingSupplier);
        OnSwitchListener onSwitchListener = () -> {
            isHumming = !isHumming;
            toggleIcon.toggle();
        };

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(toggleIcon.getSize(), toggleIcon.getSize(), Gravity.CENTER | Gravity.BOTTOM);
        params.setMargins(0, 0, 0, Screen.dp(getContext(), 24));
        SwitchView switchView = new SwitchView(singingSupplier, getContext());
        switchView.initActive(isHumming);
        switchView.setOnSwitchListener(onSwitchListener);
        root.addView(switchView, params);
        root.addView(toggleIcon, params);
        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        recordView.setDefault();
    }
}
