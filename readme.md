**RecyclerView 帮助类：**

* [**`ItemClickHelper`**](./helper/src/main/java/recyclerview/helper/ItemClickHelper.java)：用于帮助处理 RecyclerView 中列表项的 **“点击/长按点击”** 事件。
* [**`SelectableHelper`**](./helper/src/main/java/recyclerview/helper/SelectableHelper.java)：用于帮助实现 RecyclerView 中列表项的 **单选与多选** 功能。
* [**`ScrollToPositionHelper`**](./helper/src/main/java/recyclerview/helper/ScrollToPositionHelper.java)：滚动到 RecyclerView 的某一 Item 位置时对该 Item 做 **背景闪动动画**。

具体的使用方法，请查看 [`Wiki`](https://github.com/jrfeng/rv-helper/wiki)。

### 使用步骤

**第 1 步**：将 `JitPack` 存储库添加到 `build` 文件中

将以下代码添加到项目根目录下的 `build.gradle` 文件中:

```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

**第 2 步**：添加依赖： [![](https://jitpack.io/v/jrfeng/rv-helper.svg)](https://jitpack.io/#jrfeng/rv-helper)

```gradle
dependencies {
    implementation 'com.github.jrfeng:rv-helper:1.0'
}
```

### LICENSE

```
MIT License

Copyright (c) 2020 jrfeng

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```