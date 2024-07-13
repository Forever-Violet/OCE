<template>
  <div style="border: 1px solid #ccc; z-index: 100">
    <!-- 工具栏 -->
    <Toolbar
    :editor="editorRef"
    :mode="props.mode"
    :defaultConfig="toolbarConfig"
    style="border-bottom: 1px solid #ccc" />
    <!-- 编辑器 -->
    <Editor
    v-model="props.modelValue"
    :style="props.style"
    :disabled="props.disabled"
    :default-config="editorConfig"
    :mode="props.mode"
    @customPaste="handleCustomPaste"
    @onCreated="handleCreated"
    @onChange="handleChange" />
  </div>
</template>

<script lang="ts" setup>
import "@wangeditor/editor/dist/css/style.css";
import { onBeforeUnmount, shallowRef, computed, PropType } from "vue";
import { Editor, Toolbar } from "@wangeditor/editor-for-vue";
import { IDomEditor, IEditorConfig } from "@wangeditor/editor";
import app from "@/constants/app";
import { getToken } from "@/utils/cache";
import { ElMessage } from "element-plus";

const props = defineProps({
  modelValue: {
    type: String,
    required: true
  },
  maxLength: {
    type: String,
    defalut: ""
  },
  mode: {
    type: String,
    default: "simple" // 可选值：[default | simple]
  },
  placeholder: {
    type: String,
    default: ""
  },
  style: {
    type: String,
    default: "height: 200px;"
  },
  toolbarKeyConfig: {
    type: Object as PropType<{ toolbarKeys: string[] }>,
    default: () => ({
      // 用于去掉不需要的工具栏配置
      toolbarKeys: [
        // 菜单 key
        'headerSelect',
        'bold', // 加粗
        'italic', // 斜体
        'through', // 删除线
        'underline', // 下划线
        'bulletedList', // 无序列表
        'numberedList', // 有序列表
        'color', // 文字颜色
        // 'insertLink', // 插入链接
        'fontSize', // 字体大小
        //'fontFamily', //字体
        'lineHeight', // 行高
        'uploadImage', // 上传图片
        'delIndent', // 缩进
        'indent', // 增进
        // 'deleteImage',//删除图片
        // 'divider', // 分割线
        'insertTable', // 插入表格
        'justifyCenter', // 居中对齐
        'justifyJustify', // 两端对齐
        'justifyLeft', // 左对齐
        'justifyRight', // 右对齐
        'codeBlock', //代码块
        'undo', // 撤销
        'redo', // 重做
        'clearStyle', // 清除格式
        'fullScreen' // 全屏
      ]
    })
  },
  disabled: {
    type: Boolean,
    default: false
  },
});

// 编辑器实例，必须用 shallowRef
const editorRef = shallowRef();

type InsertFnType = (url: string, alt: string, href: string) => void;

const toolbarConfig = computed(() => ({
  toolbarKeys: props.toolbarKeyConfig.toolbarKeys
}));

// 编辑器配置
const editorConfig: Partial<IEditorConfig> = {
  placeholder: props.placeholder,
  readOnly: props.disabled,
  maxLength: Number(props.maxLength),
  MENU_CONF: {
    uploadImage: {
      server: `${app.api}/sys/oss/upload?token=${getToken()}`, // 上传地址
      fieldName: "file",
      // 自定义插入图片
      customInsert(res: any, insertFn: InsertFnType) {
        // res 即服务端的返回结果
        // 从 res 中找到 url alt href ，然后插图图片
        insertFn(res.data.src, "", "");
      }
    }
  },


};

const handleCustomPaste = (editor: IDomEditor, event: ClipboardEvent) => {
  event.preventDefault(); // 阻止默认粘贴
  // 获取粘贴板中的内容，处理HTML文本
  let content = event.clipboardData?.getData('text/html');

  if (!content) return false;

  const tempElement = document.createElement('span');
  tempElement.id = 'tempElement'
  tempElement.innerHTML = content;
  // 去除空行
  //const cleanedContent = tempElement.innerText.replace(/^\s*$(?:\r\n?|\n)/gm, '');
  console.log("editor--content"+content);
  
  //console.log("editor--str"+cleanedContent);

  const span = document.createElement('span');
  span.id = "tempSpan"
  span.innerText = tempElement.innerText;

  // 将处理后的纯文本内容插入到编辑器中
  editor.insertText(span.innerText);
  // 完成后，通过ID找到span元素并删除它
  removeElementById('tempElement');
  removeElementById('tempspan');
  //editor.insertText(cleanedContent);
  // // 格式清理逻辑
  // let str = content;
  // str = str.replace(/<\/?[^>]*>/g, "")
  //         .replace(/<xml>[\s\S]*?<\/xml>/gi, "")
  //         .replace(/<style>[\s\S]*?<\/style>/gi, "")
  //         .replace(/&nbsp;/gi, "")
  //         .replace(/[ | ]*\n/g, "")
  // console.log("editor--content", content);
  // //str = "<span>"+str+"</span>";
  // console.log("editor--str", str);
  // const span = document.createElement('span');
  // span.id = "tempSpan"
  // span.innerText = str;

  // // 将处理后的纯文本内容插入到编辑器中
  // //editor.dangerouslyInsertHtml(str);
  // editor.insertText(span.innerText);
  // // 完成后，通过ID找到span元素并删除它
  // const spanToRemove = document.getElementById("tempSpan");
  // if (spanToRemove && spanToRemove.parentNode) {
  //   spanToRemove.parentNode.removeChild(spanToRemove);
  // }
};

const removeElementById = (elementId: string) => {
  const element = document.getElementById(elementId);
  if (element && element.parentNode) {
    element.parentNode.removeChild(element);
  }
};

// 组件销毁时，也及时销毁编辑器
onBeforeUnmount(() => {
  const editor = editorRef.value;
  if (editor == null) {
    return;
  }
  editor.destroy();
});

const handleCreated = (editor: IDomEditor) => {
  editorRef.value = editor;
    // 查看所有工具栏key
  // console.log(editor.getAllMenuKeys());
};

// 编辑器change事件触发
const emit = defineEmits(["update:modelValue"]);
const handleChange = (editor: IDomEditor) => {
  // const totalLens = getTextLength(editor.getHtml());
  // console.log("maxLength:"+props.maxLength+", totalLens:  " + totalLens);
  // if (props.maxLength && totalLens > Number(props.maxLength)) {
  //   console.log("!!!!!!!!!maxlength:"+props.maxLength+", totalLens:  " + totalLens);

  //   ElMessage.error({
  //     message: "输入字符过长!",
  //     duration: 1000
  //   });
  // } else {
  //   emit("update:modelValue", editor.getHtml());

  // }
  emit("update:modelValue", editor.getHtml());

};

/** 现在使用自带的文本输入限制
 * 计算富文本输入框内输入字符的长度（不包含p、img、video等标签，仅计算输入文字的长度）
 */
 const getTextLength = (newHtml: string) => {
  return newHtml.replace(/<[^>]+>/g, '').length;
 }
</script>
