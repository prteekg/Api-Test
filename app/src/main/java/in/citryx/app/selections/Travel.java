package in.citryx.app.selections;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import in.citryx.app.R;

/**
 * Created by userx on 8/5/17.
 */

public class Travel extends Fragment implements View.OnClickListener{
    
    private Button checkNowButton;
    private final String TAG = "TravelFragment";
    ButtonClickInterface buttonClickInterface;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        buttonClickInterface = (ButtonClickInterface) getActivity();
        checkNowButton = (Button) getActivity().findViewById(R.id.checkButtonTravel);
        checkNowButton.setOnClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.travel_fragment, container, false);
    }

    @Override
    public void onClick(View v) {
        buttonClickInterface.buttonClicked("travel_fragment");
    }
}

