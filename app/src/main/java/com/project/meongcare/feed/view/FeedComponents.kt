package com.project.meongcare.feed.view

import android.widget.ImageView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import com.bumptech.glide.Glide
import com.project.meongcare.R
import com.project.meongcare.designsystem.theme.Gray2
import com.project.meongcare.designsystem.theme.Gray3
import com.project.meongcare.designsystem.theme.Gray4
import com.project.meongcare.designsystem.theme.Gray5
import com.project.meongcare.designsystem.theme.Main4
import com.project.meongcare.designsystem.theme.SemobanTypography
import com.project.meongcare.designsystem.theme.White
import com.project.meongcare.feed.model.entities.FeedPartRecord

@Composable
internal fun FeedGlideImage(
    model: Any,
    modifier: Modifier = Modifier,
) {
    AndroidView(
        modifier = modifier,
        factory = { context -> ImageView(context) },
        update = { imageView ->
            Glide.with(imageView)
                .load(model)
                .into(imageView)
        },
    )
}

@Composable
internal fun FeedPartItem(
    feedPartRecord: FeedPartRecord,
    modifier: Modifier = Modifier,
) {
    val period =
        if (feedPartRecord.endDate == null) {
            "${feedPartRecord.startDate}~ 모름"
        } else {
            "${feedPartRecord.startDate}~ ${feedPartRecord.endDate}"
        }

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .border(1.dp, Gray3, RoundedCornerShape(10.dp)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val imageModifier =
            Modifier
                .padding(start = 24.dp, top = 15.dp, bottom = 15.dp)
                .size(80.dp)
                .clip(RoundedCornerShape(10.dp))
        if (feedPartRecord.feedImageURL.isNullOrEmpty()) {
            Image(
                painter = painterResource(R.drawable.feed_item_default_image),
                contentDescription = null,
                modifier = imageModifier,
            )
        } else {
            FeedGlideImage(
                model = feedPartRecord.feedImageURL,
                modifier = imageModifier,
            )
        }
        Column(modifier = Modifier.padding(start = 24.dp)) {
            Text(
                text = feedPartRecord.brandName,
                style = SemobanTypography.body2Regular,
                color = Gray4,
            )
            Text(
                text = feedPartRecord.feedName,
                style = SemobanTypography.body1Medium,
                modifier = Modifier.padding(top = 1.dp),
            )
            Text(
                text = period,
                style = SemobanTypography.body3Regular,
                color = Gray5,
            )
        }
    }
}

@Composable
internal fun FeedGuideButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Gray2,
    borderColor: Color? = null,
) {
    var boxModifier =
        modifier
            .fillMaxWidth()
            .background(backgroundColor, RoundedCornerShape(5.dp))
    if (borderColor != null) {
        boxModifier = boxModifier.border(1.dp, borderColor, RoundedCornerShape(5.dp))
    }
    Text(
        text = text,
        style = SemobanTypography.bottom2SemiBold,
        color = Gray5,
        textAlign = TextAlign.Center,
        modifier =
            boxModifier
                .clickable { onClick() }
                .padding(vertical = 12.dp),
    )
}

@Composable
internal fun FeedConfirmDialog(
    title: String,
    description: String,
    confirmText: String,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
) {
    Dialog(onDismissRequest = onCancel) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = White,
            shape = RoundedCornerShape(10.dp),
        ) {
            Column(modifier = Modifier.padding(vertical = 20.dp)) {
                Text(
                    text = title,
                    style = SemobanTypography.title3SemiBold,
                    modifier = Modifier.padding(start = 24.dp),
                )
                Text(
                    text = description,
                    style = SemobanTypography.body2Regular,
                    color = Gray4,
                    modifier = Modifier.padding(start = 24.dp, top = 4.dp, end = 24.dp),
                )
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 30.dp, end = 23.dp),
                    horizontalArrangement = Arrangement.End,
                ) {
                    FeedDialogButton(
                        text = "취소",
                        backgroundColor = Gray2,
                        textColor = Gray5,
                        onClick = onCancel,
                    )
                    FeedDialogButton(
                        text = confirmText,
                        backgroundColor = Main4,
                        textColor = White,
                        onClick = onConfirm,
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun FeedDialogButton(
    text: String,
    backgroundColor: Color,
    textColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = SemobanTypography.bottom2SemiBold,
        color = textColor,
        modifier =
            modifier
                .background(backgroundColor, RoundedCornerShape(5.dp))
                .clickable { onClick() }
                .padding(horizontal = 32.dp, vertical = 7.dp),
    )
}
