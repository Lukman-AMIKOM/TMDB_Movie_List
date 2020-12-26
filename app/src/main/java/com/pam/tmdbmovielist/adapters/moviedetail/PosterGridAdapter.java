package com.pam.tmdbmovielist.adapters.moviedetail;

import android.graphics.drawable.Drawable;
import android.transition.TransitionSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.pam.tmdbmovielist.R;
import com.pam.tmdbmovielist.ui.fragments.FullscreenPagerFragment;
import com.pam.tmdbmovielist.ui.fragments.MovieDetailFragment;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class PosterGridAdapter extends RecyclerView.Adapter<PosterGridAdapter.ImageViewHolder> {
    
    private final RequestManager requestManager;
    private final ViewHolderListener viewHolderListener;
    private final String[] posterUrls;
    
    public PosterGridAdapter(MovieDetailFragment movieDetailFragment) {
        this.requestManager = Glide.with(movieDetailFragment);
        this.viewHolderListener = new ViewHolderListenerImpl(movieDetailFragment);
        this.posterUrls = movieDetailFragment.getPosterUrls();
    }
    
    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_poster, parent, false);
        return new ImageViewHolder(view, requestManager, viewHolderListener);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        holder.onBind();
    }
    
    @Override
    public int getItemCount() {
        return posterUrls.length;
    }
    
    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        
        private final ImageView image;
        private final ProgressBar progressBar;
        private final RequestManager requestManager;
        private final ViewHolderListener viewHolderListener;
        
        public ImageViewHolder(@NonNull View itemView, RequestManager requestManager,
                               ViewHolderListener viewHolderListener) {
            super(itemView);
            image = itemView.findViewById(R.id.img_posters);
            progressBar = itemView.findViewById(R.id.pb_grid_poster);
            this.requestManager = requestManager;
            this.viewHolderListener = viewHolderListener;
            itemView.findViewById(R.id.card_view).setOnClickListener(this);
        }
        
        void onBind() {
            int adapterPosition = getAdapterPosition();
            setImage(adapterPosition);
            image.setTransitionName(posterUrls[adapterPosition]);
        }
        
        void setImage(final int adapterPosition) {
            progressBar.setVisibility(View.VISIBLE);
            requestManager
                    .load(posterUrls[adapterPosition])
                    .apply(new RequestOptions().override(300, 450))
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            image.setImageDrawable(ContextCompat.getDrawable(image.getContext(), R.drawable.ic_broken_image));
                            viewHolderListener.onLoadCompleted(image, adapterPosition);
                            return false;
                        }
                        
                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            viewHolderListener.onLoadCompleted(image, adapterPosition);
                            return false;
                        }
                    })
                    .into(image);
        }
        
        @Override
        public void onClick(View v) {
            viewHolderListener.onItemClicked(v, getAdapterPosition());
        }
    }
    
    private interface ViewHolderListener {
        
        void onLoadCompleted(ImageView view, int adapterPosition);
        
        void onItemClicked(View view, int adapterPosition);
    }
    
    private static class ViewHolderListenerImpl implements ViewHolderListener {
        
        private final MovieDetailFragment movieDetailFragment;
        private final AtomicBoolean enterTransitionStarted;
        
        public ViewHolderListenerImpl(MovieDetailFragment movieDetailFragment) {
            this.movieDetailFragment = movieDetailFragment;
            this.enterTransitionStarted = new AtomicBoolean();
        }
        
        @Override
        public void onLoadCompleted(ImageView view, int adapterPosition) {
            if (movieDetailFragment.getCurrentPosition() != adapterPosition) {
                return;
            }
            
            if (enterTransitionStarted.getAndSet(true)) {
                return;
            }
            
            movieDetailFragment.startPostponedEnterTransition();
        }
        
        @Override
        public void onItemClicked(View view, int adapterPosition) {
            movieDetailFragment.setCurrentPosition(adapterPosition);
            
            ((TransitionSet) Objects.requireNonNull(movieDetailFragment.getExitTransition())).excludeTarget(view, true);
            
            ImageView transitioningView = view.findViewById(R.id.img_posters);
            if (movieDetailFragment.getFragmentManager() != null) {
                movieDetailFragment.getFragmentManager()
                        .beginTransaction()
                        .setReorderingAllowed(true)
                        .addSharedElement(transitioningView, transitioningView.getTransitionName())
                        .replace(R.id.main_container, new FullscreenPagerFragment(movieDetailFragment), FullscreenPagerFragment.class.getSimpleName())
                        .addToBackStack(null)
                        .commit();
            }
        }
    }
}
