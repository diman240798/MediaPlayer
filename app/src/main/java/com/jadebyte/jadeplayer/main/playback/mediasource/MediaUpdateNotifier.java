package com.jadebyte.jadeplayer.main.playback.mediasource;

import com.jadebyte.jadeplayer.main.common.view.BaseMediaStoreViewModel;

public class MediaUpdateNotifier {
    private BaseMediaStoreViewModel baseMediaStoreViewModel;

    public BaseMediaStoreViewModel getBaseMediaStoreViewModel() {
        return baseMediaStoreViewModel;
    }

    public void setBaseMediaStoreViewModel(BaseMediaStoreViewModel baseMediaStoreViewModel) {
        this.baseMediaStoreViewModel = baseMediaStoreViewModel;
    }

    public void update() {
        if (baseMediaStoreViewModel!= null) baseMediaStoreViewModel.update();
    }
}
