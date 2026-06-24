## 开篇就来了一个问题: 为什么子类一定要实现父类的任务？为什么这么设计？

简单来讲，这是面向对象编程（OOP）的**设计哲学根基**。

现实里:“鸵鸟虽然是鸟，但不会飞也很正常啊”
程序里:“鸵鸟继承了鸟类，那它就得会飞!”

在编程里，这种期望不是“道德要求”，而是**“契约要求”**。

具体来说，之所以期望子类一定要完成父类的事情，背后有且只有一个理由：

在编程中，使用继承的主要目的，是为了**多态**——即用一个统一的变量（比如父类类型）去操作不同的子类对象。

- 当你把对象声明为`Animal`类型时，调用`animal.eat()`，你**必须**信任所有子类都能完成“吃”这个动作。
- 如果某个子类（比如`StoneAnimal`石头动物）继承了`Animal`，但`eat()`方法里抛出一个“我不吃东西”的异常，那么所有写`animal.eat()`的代码都会在运行时莫名其妙地崩溃。

**编程期望子类完成父类的事情，本质上是期望“类型承诺”得到兑现**——既然你声称你是`Animal`，你就必须履行`Animal`的所有义务。

好了，现在你可以回去看doc_01了。

---

## 不可变设计也太简单粗暴了吧？

对的，只读不写就是这样。
类似java的String也是这样，要修改的时候后直接new新的对象。

虽然看起来少了修改的功能，但实际上在很多情况下都很有用！

---

## 没看懂这一段 实践建议 里面的示例

贴一下原示例:
```java
// 不好：继承可能违反 LSP
class Stack extends ArrayList { }

// 更好：组合
class Stack {
    private List<Object> elements = new ArrayList<>();
    
    public void push(Object item) {
        elements.add(item);
    }
    
    public Object pop() {
        return elements.remove(elements.size() - 1);
    }
}
```

这里面:  
- Stack 类里有一个字段 elements，它的类型是 List  
- Stack 不继承 ArrayList，而是内部持有一个 ArrayList  
- Stack 的所有方法都是通过调用 elements 的方法来实现的  

这就叫组合：Stack 由 List 组合而成。

### 为什么不用继承，而要用组合？

#### ❌ 坏设计：继承 `ArrayList`
```java
class Stack extends ArrayList {  // Stack 是一个 ArrayList
    // 继承了 add(), remove(), get(), set() 等所有方法
}

Stack s = new Stack();
s.add(5);        // ✅ 可以添加
s.add(10);       // ✅ 可以添加
s.remove(0);     // ✅ 可以从中间删除
s.get(2);        // ✅ 可以随机访问
```

**问题**：`Stack`（栈）应该是"后进先出"（LIFO），但继承 `ArrayList` 后，你可以：
- 从中间删除元素（违反栈的规则）
- 随机访问任意位置的元素（违反栈的规则）
- 在任意位置插入元素（违反栈的规则）

**这就违反了里氏替换原则**：父类 `ArrayList` 能做到的事（随机访问），子类 `Stack` 做不到（或者不应该做）。

---

#### ✅ 好设计：组合 `ArrayList`
```java
class Stack {
    private List<Object> elements = new ArrayList<>();  // 组合
    
    public void push(Object item) {
        elements.add(item);  // 只暴露需要的方法
    }
    
    public Object pop() {
        return elements.remove(elements.size() - 1);
    }
    
    // 没有暴露 add(index, item)、get(index) 等方法
}

Stack s = new Stack();
s.push(5);    // ✅ 只能 push
s.push(10);   // ✅ 只能 push
s.pop();      // ✅ 只能 pop
// s.add(5)   // ❌ 编译错误！没有这个方法
```

**好处**：
- 你只暴露了 `push` 和 `pop`，完美符合栈的 LIFO 语义
- 外部无法破坏栈的规则
- 你随时可以更换内部的 `List` 为 `LinkedList` 或其他结构，而外部代码不受影响

---

#### 三、组合 vs 继承的可视化对比

##### 继承（"是一个"）：
```
       ArrayList
           ↑
           |
        Stack
```
`Stack` 继承了 `ArrayList` 的所有公开方法，包括不该有的。

##### 组合（"有一个"）：
```
        Stack
          |
      拥有 (has-a)
          |
      ArrayList
```
`Stack` 内部有一个 `ArrayList`，但只暴露自己需要的接口。

---

#### 六、什么时候用组合，什么时候用继承？

| 场景 | 推荐 | 原因 |
|------|------|------|
| "是一个"（is-a）关系明确，且行为完全兼容 | **继承** | 如 `Dog extends Animal` |
| "有一个"（has-a）关系 | **组合** | 如 `Car` 有 `Engine` |
| 想复用代码，但类型关系不明确 | **组合** | 更灵活，更安全 |
| 需要多态（父类引用指向子类对象） | **继承** | 组合无法实现多态 |
| 想避免暴露不该暴露的方法 | **组合** | 只暴露需要的接口 |

---

#### 总结

 `Stack` 示例中：
- **组合体现在**：`private List<Object> elements = new ArrayList<>();`
- **组合的好处**：只暴露 `push`/`pop`，隐藏了 `add(index,item)` 等不该有的方法
- **为什么不继承**：因为 `Stack` 不是一个 `ArrayList`（虽然底层用了它）

这就是经典的**"组合优于继承"**原则（《设计模式》书中的第一条建议）!
