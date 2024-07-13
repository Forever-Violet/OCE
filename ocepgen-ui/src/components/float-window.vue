<!-- Component Definition -->
<template>
    <div @mouseover="contentController.show" @mouseleave="contentController.hide">
        <el-button :class="props.buttonClass" type="primary" style="width: 70px !important;">
            <div :class="props.headerClass" 
                @mouseover="contentController.show" 
                @mouseleave="contentController.hide"
            >
                <slot name="header"></slot>
                <Transition :name="props.animation">
                <div v-show="!contentController.hidden" 
                    @mouseover="contentController.show"
                    @mouseleave="contentController.hide"
                >
                    <div 
                        class="float-window-content" 
                        style="position: absolute;" 
                    >
                        <slot name="content"></slot>
                    </div>
                </div>
            </Transition>
            </div>

        </el-button>
    </div>
</template>

<script setup lang="ts">
import { defineProps, withDefaults, reactive } from 'vue';

interface FloatWindowProps {
    buttonClass?: string
    headerClass?: string
    contentClass?: string
    hiddeDelay?: number
    animation?: string
}

const props = withDefaults(defineProps<FloatWindowProps>(), {
    buttonClass: 'float-window-button',
    headerClass: 'float-window-header',
    contentClass: 'float-window-content',
    hiddeDelay: 300,
    animation: 'main-fade'
});

// acquire handle of elements

const contentController = reactive({
    hidden: true,
    hideTimerID: -1,
    show() {
        this.hidden = false;
        if (this.hideTimerID != -1) {
            clearInterval(this.hideTimerID);
            this.hideTimerID = -1;
            console.log(props.contentClass);

        }
    },
    hide() {
        this.hideTimerID = setTimeout(() => {
            this.hidden = true;
        }, props.hiddeDelay);
    }
});

</script>

<!-- style -->
<style>
:root {
    --animation-during: .5s;
}

/* 虽然名字很奇怪，但是这个类代表最外层的那家伙 */
.float-window-button {
    height: fit-content;
    width: fit-content;
    /*font-size: 15px;*/
}

/* 一般情况下，我们认为 content 依附于 header */
.float-window-header {
    position: relative;
}

.float-window-content {
    position: absolute;
    bottom: -170px;
    left: -21px;
    border-radius: .9em;
}
</style>