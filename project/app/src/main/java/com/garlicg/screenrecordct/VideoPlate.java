package com.garlicg.screenrecordct;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.garlicg.screenrecordct.data.AppStorage;
import com.garlicg.screenrecordct.data.ThumbCache;
import com.garlicg.screenrecordct.plate.Plate;
import com.garlicg.screenrecordct.util.ViewFinder;

import java.text.SimpleDateFormat;
import java.util.Date;

public class VideoPlate extends Plate<VideoPlate.VH>{

    public static VideoPlate newInstance(VideoModel video , Handler handler){
        VideoPlate plate = new VideoPlate();
        plate.video = video;
        plate.setHandler(handler);
        return plate;
    }

    public static class VH extends RecyclerView.ViewHolder{
        final ImageView thumbnail;
        final TextView title;
        final TextView duration;
        final TextView size;
        final TextView wh;
        final View delete;

        public VH(View itemView) {
            super(itemView);

            thumbnail = ViewFinder.byId(itemView , R.id.thumbnail);
            title = ViewFinder.byId(itemView , R.id.title);
            duration = ViewFinder.byId(itemView , R.id.duration);
            size = ViewFinder.byId(itemView , R.id.size);
            wh = ViewFinder.byId(itemView , R.id.wh);
            delete = ViewFinder.byId(itemView , R.id.delete);
        }
    }


    @Override
    protected VH onCreateViewHolder(LayoutInflater inflater, ViewGroup parent) {
        return new VH(inflater.inflate(R.layout.plate_video ,parent , false));
    }


    public VideoModel video;

    @Override
    protected void onBind(Context context, VH vh) {
        super.onBind(context, vh);

        String added = DateFormat.getDateFormat(context).format(new Date(video.dataAdded * 1000));
        vh.title.setText("" + added);
        vh.duration.setText("" + video.duration);
        vh.size.setText("" + video.size);
        vh.wh.setText("w" + video.width + "x h" + video.height);

        bindThumbnail(context, vh.thumbnail);
    }



    /**
     * カットインアプリアイコンを非同期で読み込む
     * 10msくらいかかる場合がある
     */
    protected void bindThumbnail(final Context context ,final ImageView appIconView) {
        // 重複排除
        Object tag = appIconView.getTag();
        if(tag != null && tag.equals(video.id)){
            return;
        }

        // タグ付け
        appIconView.setTag(video.id);
        Bitmap bitmap = ThumbCache.getInstance().get(video.id);
        if(bitmap != null){
            appIconView.setImageBitmap(bitmap);
            return;
        }

        // clear
        appIconView.setImageBitmap(null);

        new Thread(new Runnable() {
            @Override
            public void run() {

                Bitmap thumb = AppStorage.Thumbnail.getThumb(context, video.id, null);
                if(thumb != null){
                    ThumbCache.getInstance().put(video.id, thumb);
                }
                else{
                    thumb = ThumbnailUtils.createVideoThumbnail(video.data, MediaStore.Video.Thumbnails.MINI_KIND);
                    if(thumb != null){
                        AppStorage.Thumbnail.saveThumb(context ,video.id , thumb);
                        ThumbCache.getInstance().put(video.id, thumb);
                    }
                }

                final Bitmap thumbnail = thumb;
                getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        setImage(appIconView ,thumbnail , true);
                    }
                });
            }
        }).start();
    }

    /**
     * 画像をsetする
     */
    protected void setImage(final ImageView iv, final Bitmap image , boolean animate){
        Object tag = iv.getTag();
        if(tag != null && !tag.equals(video.id)){
            return;
        }

        iv.setImageBitmap(image);
        if(animate && image != null){
            Animation anim = new AlphaAnimation(0f , 1f);
            anim.setDuration(200);
            iv.startAnimation(anim);
        }
    }



}
