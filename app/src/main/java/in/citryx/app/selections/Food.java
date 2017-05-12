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

public class Food extends Fragment implements View.OnClickListener{

    private Button eatNowButton;
    private final String TAG = "FoodFragment";
    ButtonClickInterface buttonClickInterface;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.food_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        buttonClickInterface = (ButtonClickInterface)getActivity();
        eatNowButton = (Button) getActivity().findViewById(R.id.foodButton);
        eatNowButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        buttonClickInterface.buttonClicked("food_fragment");
    }
}
