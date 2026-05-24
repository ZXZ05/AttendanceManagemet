import {
  ElAlert,
  ElButton,
  ElCalendar,
  ElCard,
  ElCarousel,
  ElCarouselItem,
  ElDatePicker,
  ElDialog,
  ElEmpty,
  ElForm,
  ElFormItem,
  ElIcon,
  ElInput,
  ElLink,
  ElMenu,
  ElMenuItem,
  ElOption,
  ElSelect,
  ElTabPane,
  ElTable,
  ElTableColumn,
  ElTabs,
  ElTag,
  ElTimePicker
} from 'element-plus'

const components = [
  ElAlert,
  ElButton,
  ElCalendar,
  ElCard,
  ElCarousel,
  ElCarouselItem,
  ElDatePicker,
  ElDialog,
  ElEmpty,
  ElForm,
  ElFormItem,
  ElIcon,
  ElInput,
  ElLink,
  ElMenu,
  ElMenuItem,
  ElOption,
  ElSelect,
  ElTabPane,
  ElTable,
  ElTableColumn,
  ElTabs,
  ElTag,
  ElTimePicker
]

export function installElementPlus(app) {
  components.forEach((component) => {
    app.use(component)
  })
}
