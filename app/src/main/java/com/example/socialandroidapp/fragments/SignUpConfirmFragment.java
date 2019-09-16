package com.example.socialandroidapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.example.socialandroidapp.R;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SignUpConfirmFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SignUpConfirmFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignUpConfirmFragment extends Fragment {
    private static final String TAG = SignUpConfirmFragment.class.getSimpleName();

    @BindView(R.id.etConfirmCode)
    @NotEmpty
    EditText etConfirmCode;


    private OnFragmentInteractionListener mListener;

    public SignUpConfirmFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignUpConfirmFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignUpConfirmFragment newInstance(String param1, String param2) {
        SignUpConfirmFragment fragment = new SignUpConfirmFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up_confirm, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void confirmSignUp(String responseCode);
    }

    @OnClick(R.id.btnSignUpConfirm)
    void doConfirm() {
        mListener.confirmSignUp(etConfirmCode.getText().toString());
    }
}
