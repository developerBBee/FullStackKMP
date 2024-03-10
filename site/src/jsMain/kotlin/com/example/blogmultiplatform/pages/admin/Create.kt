package com.example.blogmultiplatform.pages.admin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.example.blogmultiplatform.components.AdminPageLayout
import com.example.blogmultiplatform.components.LinkPopup
import com.example.blogmultiplatform.components.MessagePopup
import com.example.blogmultiplatform.models.ApiResponse
import com.example.blogmultiplatform.models.Category
import com.example.blogmultiplatform.models.Constants.POST_ID_PARAM
import com.example.blogmultiplatform.models.ControlStyle
import com.example.blogmultiplatform.models.EditorControl
import com.example.blogmultiplatform.models.Post
import com.example.blogmultiplatform.models.Theme
import com.example.blogmultiplatform.navigation.Screen
import com.example.blogmultiplatform.styles.EditorControlStyle
import com.example.blogmultiplatform.util.Constants
import com.example.blogmultiplatform.util.Constants.FONT_FAMILY
import com.example.blogmultiplatform.util.Id
import com.example.blogmultiplatform.util.addPost
import com.example.blogmultiplatform.util.applyControlStyle
import com.example.blogmultiplatform.util.applyStyle
import com.example.blogmultiplatform.util.fetchSelectedPost
import com.example.blogmultiplatform.util.getEditor
import com.example.blogmultiplatform.util.getSelectedText
import com.example.blogmultiplatform.util.isUserLoggedIn
import com.example.blogmultiplatform.util.noBorder
import com.example.blogmultiplatform.util.updatePost
import com.varabyte.kobweb.browser.file.loadDataUrlFromDisk
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.css.Resize
import com.varabyte.kobweb.compose.css.ScrollBehavior
import com.varabyte.kobweb.compose.css.Visibility
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.attrsModifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.classNames
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.cursor
import com.varabyte.kobweb.compose.ui.modifiers.disabled
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxHeight
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.fontFamily
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.height
import com.varabyte.kobweb.compose.ui.modifiers.id
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.maxHeight
import com.varabyte.kobweb.compose.ui.modifiers.maxWidth
import com.varabyte.kobweb.compose.ui.modifiers.onClick
import com.varabyte.kobweb.compose.ui.modifiers.onKeyDown
import com.varabyte.kobweb.compose.ui.modifiers.overflow
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.resize
import com.varabyte.kobweb.compose.ui.modifiers.scrollBehavior
import com.varabyte.kobweb.compose.ui.modifiers.visibility
import com.varabyte.kobweb.compose.ui.thenIf
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.forms.Input
import com.varabyte.kobweb.silk.components.forms.Switch
import com.varabyte.kobweb.silk.components.forms.SwitchSize
import com.varabyte.kobweb.silk.components.graphics.Image
import com.varabyte.kobweb.silk.components.layout.SimpleGrid
import com.varabyte.kobweb.silk.components.layout.numColumns
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.components.style.toModifier
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.theme.breakpoint.rememberBreakpoint
import kotlinx.browser.document
import kotlinx.browser.localStorage
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Li
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.dom.TextArea
import org.jetbrains.compose.web.dom.Ul
import org.w3c.dom.get
import kotlin.js.Date

data class CreatePageUiEvent(
    val id: String = "",
    val title: String = "",
    val subtitle: String = "",
    val thumbnail: String = "",
    val thumbnailFileName: String = "",
    val thumbnailInputEnabled: Boolean = true,
    val content: String = "",
    val category: Category = Category.Programing,
    val buttonText: String = "Create",
    val popular: Boolean = false,
    val main: Boolean = false,
    val sponsored: Boolean = false,
    val editorVisibility: Boolean = true,
    val messagePopup: Boolean = false,
    val message: String = "",
    val linkPopup: Boolean = false,
    val imagePopup: Boolean = false,
)

private val INIT_UI_EVENT = CreatePageUiEvent()

@Page
@Composable
fun CreatePage() {
    isUserLoggedIn {
        CreateScreen()
    }
}

@Composable
fun CreateScreen() {
    val scope = rememberCoroutineScope()
    val context = rememberPageContext()

    val breakpoint = rememberBreakpoint()
    val isLarge: Boolean = (breakpoint > Breakpoint.MD)

    var uiEvent by remember { mutableStateOf(INIT_UI_EVENT) }

    val hasPostIdParam = remember(key1 = context.route) {
        context.route.params.containsKey(POST_ID_PARAM)
    }

    LaunchedEffect(key1 = hasPostIdParam) {
        if (!hasPostIdParam) {
            uiEvent = INIT_UI_EVENT
            return@LaunchedEffect
        }

        val postId = context.route.params[POST_ID_PARAM] ?: ""
        val response = fetchSelectedPost(id = postId)
        if (response is ApiResponse.Success) {
            response.data.run {
                println(this)
                uiEvent = uiEvent.copy(
                    id = _id,
                    title = title,
                    subtitle = subtitle,
                    content = content,
                    category = category,
                    buttonText = "Update",
                    thumbnail = thumbnail,
                    thumbnailFileName = thumbnail,
                    main = main,
                    popular = popular,
                    sponsored = sponsored,
                )
            }
        }
    }

    AdminPageLayout {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .margin(topBottom = 50.px)
                .padding(left = if (isLarge) Constants.SIDE_PANEL_WIDTH.px else 0.px),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .maxWidth(700.px),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                SimpleGrid(numColumns = numColumns(base = 1, sm = 3)) {
                    Row(
                        modifier = Modifier
                            .margin(
                                right = if (breakpoint < Breakpoint.SM) 0.px else 24.px,
                                bottom = if (breakpoint < Breakpoint.SM) 12.px else 0.px
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Switch(
                            modifier = Modifier.margin(right = 8.px),
                            checked = uiEvent.popular,
                            onCheckedChange = { uiEvent = uiEvent.copy(popular = it) },
                            size = SwitchSize.LG
                        )
                        SpanText(
                            modifier = Modifier
                                .fontSize(14.px)
                                .fontFamily(FONT_FAMILY)
                                .color(Theme.HalfBlack.rgb),
                            text = "Popular"
                        )
                    }
                    Row(
                        modifier = Modifier
                            .margin(
                                right = if (breakpoint < Breakpoint.SM) 0.px else 24.px,
                                bottom = if (breakpoint < Breakpoint.SM) 12.px else 0.px
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Switch(
                            modifier = Modifier.margin(right = 8.px),
                            checked = uiEvent.main,
                            onCheckedChange = { uiEvent = uiEvent.copy(main = it) },
                            size = SwitchSize.LG
                        )
                        SpanText(
                            modifier = Modifier
                                .fontSize(14.px)
                                .fontFamily(FONT_FAMILY)
                                .color(Theme.HalfBlack.rgb),
                            text = "Main"
                        )
                    }
                    Row(
                        modifier = Modifier
                            .margin(
                                right = if (breakpoint < Breakpoint.SM) 0.px else 24.px,
                                bottom = if (breakpoint < Breakpoint.SM) 12.px else 0.px
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Switch(
                            modifier = Modifier.margin(right = 8.px),
                            checked = uiEvent.sponsored,
                            onCheckedChange = { uiEvent = uiEvent.copy(sponsored = it) },
                            size = SwitchSize.LG
                        )
                        SpanText(
                            modifier = Modifier
                                .fontSize(14.px)
                                .fontFamily(FONT_FAMILY)
                                .color(Theme.HalfBlack.rgb),
                            text = "Sponsored"
                        )
                    }
                }
                Input(
                    type = InputType.Text,
                    value = uiEvent.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.px)
                        .margin(topBottom = 12.px)
                        .backgroundColor(Theme.LightGray.rgb)
                        .borderRadius(r = 4.px)
                        .noBorder()
                        .fontFamily(FONT_FAMILY),
                    placeholder = "Title",
                    onValueChanged = { uiEvent = uiEvent.copy(title = it) }
                )
                Input(
                    type = InputType.Text,
                    value = uiEvent.subtitle,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.px)
                        .margin(bottom = 12.px)
                        .backgroundColor(Theme.LightGray.rgb)
                        .borderRadius(r = 4.px)
                        .noBorder()
                        .fontFamily(FONT_FAMILY),
                    placeholder = "Subtitle",
                    onValueChanged = { uiEvent = uiEvent.copy(subtitle = it) }
                )
                CategoryDropdown(
                    selectedCategory = uiEvent.category,
                    onCategorySelect = { uiEvent = uiEvent.copy(category = it) }
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .margin(topBottom = 12.px),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Switch(
                        modifier = Modifier.margin(right = 8.px),
                        checked = !uiEvent.thumbnailInputEnabled,
                        onCheckedChange = { uiEvent = uiEvent.copy(thumbnailInputEnabled = !it) },
                        size = SwitchSize.MD
                    )
                    SpanText(
                        modifier = Modifier
                            .fontSize(14.px)
                            .fontFamily(FONT_FAMILY)
                            .color(Theme.HalfBlack.rgb),
                        text = "Paste an Image URL instead"
                    )
                }
                ThumbnailUploader(
                    thumbnail = uiEvent.thumbnailFileName,
                    thumbnailInputEnabled = uiEvent.thumbnailInputEnabled,
                    onThumbnailSelect = { filename, file ->
                        uiEvent = uiEvent.copy(thumbnail = file, thumbnailFileName = filename)
                    }
                )
                EditorControls(
                    breakpoint = breakpoint,
                    editorVisibility = uiEvent.editorVisibility,
                    editorVisibilityChange = {
                        uiEvent = uiEvent.copy(editorVisibility = !uiEvent.editorVisibility)
                    },
                    onLinkClick = { uiEvent = uiEvent.copy(linkPopup = true) },
                    onImageClick = { uiEvent = uiEvent.copy(imagePopup = true) },
                    onApplied = { uiEvent = uiEvent.copy(content = getEditor().value) },
                )
                Editor(
                    text = uiEvent.content,
                    editorVisibility = uiEvent.editorVisibility,
                    onInput = { uiEvent = uiEvent.copy(content = it) }
                )
                CreateButton(text = uiEvent.buttonText) {
                    if (!uiEvent.thumbnailInputEnabled) {
                        uiEvent = uiEvent.copy(thumbnail = "")
                    }
                    if (
                        uiEvent.title.isNotEmpty() &&
                        uiEvent.subtitle.isNotEmpty() &&
                        uiEvent.thumbnail.isNotEmpty() &&
                        uiEvent.content.isNotEmpty()
                    ) {
                        scope.launch {
                            Post(
                                _id = uiEvent.id,
                                author = localStorage.get("username").toString(),
                                title = uiEvent.title,
                                subtitle = uiEvent.subtitle,
                                date = Date.now().toLong(),
                                thumbnail = uiEvent.thumbnail,
                                content = uiEvent.content,
                                category = uiEvent.category,
                                popular = uiEvent.popular,
                                main = uiEvent.main,
                                sponsored = uiEvent.sponsored,
                            ).let {
                                hasPostIdParam to if (hasPostIdParam) {
                                    updatePost(it)
                                } else {
                                    addPost(it)
                                }
                            }.also { (updated, result) -> if (result) {
                                context.router.navigateTo(Screen.AdminSuccess.getRoute(updated))
                            } }
                        }
                    } else {
                        uiEvent = uiEvent.copy(
                            messagePopup = true,
                            message = "Please fill out all fields."
                        )
                    }
                }
            }
        }
    }
    if (uiEvent.messagePopup) {
        MessagePopup(message = uiEvent.message) {
            uiEvent = uiEvent.copy(messagePopup = false)
        }
    }
    if (uiEvent.linkPopup) {
        LinkPopup(
            controlPopup = EditorControl.Link,
            onDialogDismiss = { uiEvent = uiEvent.copy(linkPopup = false) },
            onAddClick = { href, title ->
                applyStyle(
                    ControlStyle.Link(
                        selectedText = getSelectedText(),
                        href = href,
                        title = title,
                    )
                )
                uiEvent = uiEvent.copy(content = getEditor().value)
            },
        )
    }
    if (uiEvent.imagePopup) {
        LinkPopup(
            controlPopup = EditorControl.Image,
            onDialogDismiss = { uiEvent = uiEvent.copy(imagePopup = false) },
            onAddClick = { url, desc ->
                applyStyle(
                    ControlStyle.Image(
                        selectedText = getSelectedText(),
                        imageLink = url,
                        desc = desc,
                    )
                )
                uiEvent = uiEvent.copy(content = getEditor().value)
            },
        )
    }
}

@Composable
fun CategoryDropdown(
    selectedCategory: Category,
    onCategorySelect: (Category) -> Unit
) {
    Box(
        modifier = Modifier
            .padding(topBottom = 12.px)
            .classNames("dropdown")
            .fillMaxWidth()
            .height(54.px)
            .backgroundColor(Theme.LightGray.rgb)
            .cursor(Cursor.Pointer)
            .attrsModifier {
                attr("data-bs-toggle", "dropdown")
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(leftRight = 20.px),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SpanText(
                modifier = Modifier
                    .fillMaxWidth()
                    .fontSize(16.px)
                    .fontFamily(FONT_FAMILY),
                text = selectedCategory.name
            )
            Box(modifier = Modifier.classNames("dropdown-toggle"))
        }
        Ul(
            attrs = Modifier
                .fillMaxWidth()
                .classNames("dropdown-menu")
                .toAttrs()
        ) {
            Category.entries.forEach { category ->
                Li {
                    A(
                        attrs = Modifier
                            .classNames("dropdown-item")
                            .color(Colors.Black)
                            .fontFamily(FONT_FAMILY)
                            .fontSize(16.px)
                            .onClick { onCategorySelect(category) }
                            .toAttrs()
                    ) {
                        Text(value = category.name)
                    }
                }
            }
        }
    }
}

@Composable
fun ThumbnailUploader(
    thumbnail: String,
    thumbnailInputEnabled: Boolean,
    onThumbnailSelect: (String, String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .margin(bottom = 20.px)
            .height(54.px)
    ) {
        Input(
            type = InputType.Text,
            value = thumbnail,
            modifier = Modifier
                .fillMaxSize()
                .margin(right = 12.px)
                .padding(leftRight = 20.px)
                .backgroundColor(Theme.LightGray.rgb)
                .borderRadius(r = 4.px)
                .noBorder()
                .fontFamily(FONT_FAMILY)
                .fontSize(16.px),
            enabled = thumbnailInputEnabled,
            placeholder = "Thumbnail",
            onValueChanged = {  }
        )
        Button(
            attrs = Modifier
                .onClick {
                    document.loadDataUrlFromDisk(
                        accept = "image/png, image/jpeg",
                        onError = { println("image load error") },
                        onLoad = { onThumbnailSelect(filename, it) }
                    )
                }
                .fillMaxHeight()
                .padding(leftRight = 24.px)
                .backgroundColor(if (thumbnailInputEnabled) Theme.Primary.rgb else Theme.Gray.rgb)
                .color(if (thumbnailInputEnabled) Theme.White.rgb else Theme.DarkGray.rgb)
                .borderRadius(r = 4.px)
                .noBorder()
                .fontFamily(FONT_FAMILY)
                .fontWeight(FontWeight.Medium)
                .fontSize(14.px)
                .thenIf(
                    condition = !thumbnailInputEnabled,
                    other = Modifier.disabled()
                )
                .toAttrs()
        ) {
            SpanText(text = "Upload")
        }
    }
}

@Composable
fun EditorControls(
    breakpoint: Breakpoint,
    editorVisibility: Boolean,
    editorVisibilityChange: () -> Unit,
    onLinkClick: () -> Unit,
    onImageClick: () -> Unit,
    onApplied: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        SimpleGrid(
            modifier = Modifier.fillMaxWidth(),
            numColumns = numColumns(base = 1, sm = 2)
        ) {
            Row(
                modifier = Modifier
                    .backgroundColor(Theme.LightGray.rgb)
                    .borderRadius(r = 4.px)
                    .height(54.px)
            ) {
                EditorControl.entries.forEach { editorControl ->
                    EditorControlView(control = editorControl) {
                        applyControlStyle(
                            editorControl = editorControl,
                            onLinkClick = onLinkClick,
                            onImageClick = onImageClick,
                            onApplied = onApplied,
                        )
                    }
                }
            }
            Box(contentAlignment = Alignment.CenterEnd) {
                Button(
                    attrs = Modifier
                        .height(54.px)
                        .thenIf(
                            condition =  breakpoint < Breakpoint.SM,
                            other = Modifier.fillMaxWidth()
                        )
                        .margin(topBottom = if (breakpoint < Breakpoint.SM) 12.px else 0.px)
                        .padding(leftRight = 24.px)
                        .borderRadius(4.px)
                        .backgroundColor(
                            if (editorVisibility) Theme.LightGray.rgb else Theme.Primary.rgb
                        )
                        .color(
                            if (editorVisibility) Theme.DarkGray.rgb else Colors.White
                        )
                        .noBorder()
                        .onClick {
                            editorVisibilityChange()
                            document.getElementById(Id.editorPreview)?.innerHTML = getEditor().value
                            js("hljs.highlightAll()") as Unit
                        }
                        .toAttrs()
                ) {
                    SpanText(
                        modifier = Modifier
                            .fontFamily(FONT_FAMILY)
                            .fontWeight(FontWeight.Medium)
                            .fontSize(14.px),
                        text = "Preview"
                    )
                }
            }
        }
    }
}

@Composable
fun EditorControlView(
    control: EditorControl,
    onClick: () -> Unit,
) {
    Box(
        modifier = EditorControlStyle.toModifier()
            .fillMaxHeight()
            .padding(leftRight = 12.px)
            .borderRadius(r = 4.px)
            .cursor(Cursor.Pointer)
            .onClick { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Image(
            src = control.icon,
            description = "${control.name} Icon"
        )
    }
}

@Composable
fun Editor(
    text: String,
    editorVisibility: Boolean,
    onInput: (String) -> Unit,
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        TextArea(
            value = text,
            attrs = Modifier
                .id(Id.editor)
                .fillMaxWidth()
                .height(400.px)
                .maxHeight(400.px)
                .resize(Resize.None)
                .margin(top = 8.px)
                .padding(all = 20.px)
                .backgroundColor(Theme.LightGray.rgb)
                .borderRadius(r = 4.px)
                .noBorder()
                .visibility(
                    visibility = if (editorVisibility) Visibility.Visible else Visibility.Hidden
                )
                .onKeyDown {
                    if (it.code == "Enter" && it.shiftKey) {
                        applyStyle(ControlStyle.Break(getSelectedText()))
                    }
                }
                .fontFamily(FONT_FAMILY)
                .fontSize(16.px)
                .toAttrs {
                    attr("placeholder", "Type here...")
                    onInput { onInput(it.value) }
                }
        )
        Div(
            attrs = Modifier
                .id(Id.editorPreview)
                .fillMaxWidth()
                .height(400.px)
                .maxHeight(400.px)
                .margin(top = 8.px)
                .padding(all = 20.px)
                .backgroundColor(Theme.LightGray.rgb)
                .borderRadius(r = 4.px)
                .noBorder()
                .visibility(
                    if (editorVisibility) Visibility.Hidden else Visibility.Visible
                )
                .overflow(Overflow.Auto)
                .scrollBehavior(ScrollBehavior.Smooth)
                .toAttrs()
        )
    }
}

@Composable
fun CreateButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        attrs = Modifier
            .onClick { onClick() }
            .fillMaxWidth()
            .height(54.px)
            .margin(top = 24.px)
            .backgroundColor(Theme.Primary.rgb)
            .color(Colors.White)
            .borderRadius(r = 4.px)
            .noBorder()
            .fontFamily(FONT_FAMILY)
            .fontSize(16.px)
            .toAttrs()
    ) {
        SpanText(text = text)
    }
}