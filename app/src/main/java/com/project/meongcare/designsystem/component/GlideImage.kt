package com.project.meongcare.designsystem.component

import android.widget.ImageView
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.bumptech.glide.Glide
import com.project.meongcare.designsystem.theme.White

// Coil 미도입 상태의 URL/Uri 이미지 로딩용 Glide 인터롭 (docs/compose-migration.md 전환 패턴 6번)
@Composable
internal fun GlideImage(
    model: Any?,
    modifier: Modifier = Modifier,
    errorRes: Int? = null,
    centerCrop: Boolean = false,
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            ImageView(context).apply {
                if (centerCrop) {
                    scaleType = ImageView.ScaleType.CENTER_CROP
                }
            }
        },
        update = { imageView ->
            var request = Glide.with(imageView).load(model)
            if (errorRes != null) {
                request = request.error(errorRes)
            }
            request.into(imageView)
        },
    )
}

@Composable
internal fun CircleGlideImage(
    model: Any?,
    errorRes: Int,
    size: Dp,
    modifier: Modifier = Modifier,
) {
    GlideImage(
        model = model,
        errorRes = errorRes,
        centerCrop = true,
        modifier =
            modifier
                .size(size)
                .clip(CircleShape)
                .border(2.dp, White, CircleShape),
    )
}
