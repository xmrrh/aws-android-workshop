package com.example.socialandroidapp;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.amplify.generated.graphql.ListPostsQuery;
import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.services.s3.AmazonS3Client;

import com.amazonaws.services.translate.AmazonTranslateAsyncClient;
import com.amazonaws.services.translate.model.TranslateTextRequest;
import com.amazonaws.services.translate.model.TranslateTextResult;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class PostAdapter extends RecyclerView.Adapter<PostAdapter.Holder> {

    private static String TAG = "dev-day-item";
    private LayoutInflater mInflater;
    private List<ListPostsQuery.Item> mData = new ArrayList<>();
    private Context ctx;

    PostAdapter(Context context) {
        ctx = context;
        this.mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public PostAdapter.Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.post_item, viewGroup, false);
        Holder holder = new Holder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull final PostAdapter.Holder holder, int i) {
        holder.bindData(mData.get(i));

        ListPostsQuery.Photo so = mData.get(i).photo();

        String bucketName = so.fragments().s3Object().bucket();
        String pName = so.fragments().s3Object().key();

        ClientConfiguration clientConfig = new ClientConfiguration();
        clientConfig.setProtocol(Protocol.HTTP);

        AmazonS3Client s3 = new AmazonS3Client(ClientFactory.getAWSCredentials(), clientConfig);

        long d = System.currentTimeMillis() + (2 * 24 * 60 * 60 * 1000);

        URL url = s3.generatePresignedUrl(bucketName, pName, new Date(d));

        final String tmpStr = url.toString();
        Log.e(TAG, "URL = " + tmpStr);

        //new DownloadImageFromInternet(ctx, holder.iv).execute(tmpStr);
        Picasso.get().load(tmpStr).into(holder.iv);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setItems(List<ListPostsQuery.Item> items) {
        mData = items;
    }



    public class Holder extends RecyclerView.ViewHolder {
        private TextView writerTxt, contentsTxt, titleTxt;

        private ImageView iv;
        private Button translateBtn;

        public Holder(View view) {
            super(view);

            iv = view.findViewById(R.id.contentImg);
            writerTxt = view.findViewById(R.id.writer);
            contentsTxt = view.findViewById(R.id.contents);
            titleTxt = view.findViewById(R.id.title);
            translateBtn = view.findViewById(R.id.translateBtn);
        }

        void bindData(final ListPostsQuery.Item item) {

            writerTxt.setText(item.author());
            contentsTxt.setText(item.content());
            titleTxt.setText(item.title());

            translateBtn.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    doTranslate(contentsTxt);
                    doTranslate(titleTxt);
                }
            });
        }
    }

    /* TRANSLATE #101 --*/
    private void doTranslate(final TextView tv) {
        AmazonTranslateAsyncClient translateAsyncClient = new AmazonTranslateAsyncClient(ClientFactory.getAWSCredentials());
        TranslateTextRequest translateTextRequest = new TranslateTextRequest()
                .withText(tv.getText().toString()).withTargetLanguageCode(Util.getLanguageCode(ctx))
                .withSourceLanguageCode("auto");

        translateAsyncClient.translateTextAsync(translateTextRequest, new AsyncHandler<TranslateTextRequest, TranslateTextResult>() {
            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Error occurred in translating the text: " + e.getLocalizedMessage());
            }

            @Override
            public void onSuccess(TranslateTextRequest request, TranslateTextResult translateTextResult) {
//              Log.d(TAG, "Original Text: " + request.getText());
//              Log.d(TAG, "Translated Text: " + translateTextResult.getTranslatedText());
                tv.setText(translateTextResult.getTranslatedText());

            }
        });

        /* -- TRANSLATE #101 */
    }

}





