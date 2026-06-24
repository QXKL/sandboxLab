# 接口隔离原则 - 自测题

完成这些题目，检验你对接口隔离原则的理解程度。

---

## 一、概念理解（选择题）

### 1. 接口隔离原则（ISP）的核心含义是什么？

A. 接口应该越小越好  
B. 客户端不应该被迫依赖它不使用的接口  
C. 每个接口只能有一个方法  
D. 接口之间应该相互隔离

<details>
<summary>查看答案</summary>

**答案**: B

**解析**: 接口隔离原则的核心是"客户端不应该被迫依赖它不使用的接口"。这意味着：
- 接口应该小而专注，只包含客户端需要的方法
- 不要创建"胖接口"强迫客户端实现不需要的方法
- 多个专用接口优于一个通用接口

选项A过于绝对，选项C是误解，选项D不是ISP的重点。
</details>

---

### 2. 以下哪种情况违反了接口隔离原则？

A. 一个接口有10个方法，客户端使用了其中8个  
B. 实现类被迫实现不需要的方法，只能抛出UnsupportedOperationException  
C. 多个小接口组合使用  
D. 客户端只依赖需要的接口

<details>
<summary>查看答案</summary>

**答案**: B

**解析**: 当实现类被迫实现不需要的方法并抛出异常时，说明违反了ISP：
- 接口包含了客户端不需要的方法（接口污染）
- 实现类无法履行接口契约（违反LSP）
- 客户端运行时才发现方法不可用

这是"胖接口"的典型问题。

其他选项：
- A: 使用80%的方法，接口粒度合理
- C: 符合ISP的做法
- D: 符合ISP的做法
</details>

---

### 3. 关于接口隔离原则，以下说法错误的是？

A. ISP 有助于降低客户端和接口的耦合  
B. ISP 要求每个接口只能有一个方法  
C. ISP 有助于避免违反里氏替换原则  
D. ISP 强调按客户端需求设计接口

<details>
<summary>查看答案</summary>

**答案**: B

**解析**: ISP不要求每个接口只能有一个方法。合理的接口应该：
- 包含3-7个高度相关的方法
- 方法应该高内聚（服务于同一个职责）
- 按功能或职责分组

过度拆分会导致接口爆炸，反而增加维护成本。

其他选项都是正确的：
- A: 客户端只依赖需要的接口，降低了耦合
- C: 接口隔离后，不会有"抛异常"的方法，自然符合LSP
- D: 这是ISP的核心思想
</details>

---

## 二、代码分析题

### 4. 判断是否违反 ISP

以下是一个用户管理系统的接口，请判断是否违反了接口隔离原则。

```java
interface UserService {
    void createUser(User user);
    User getUser(String userId);
    Set<String> getUserRoles(String userId);
    void logUserAction(String userId, String action);
    void assignRole(String userId, String role);
    void revokeRole(String userId, String role);
    List<AuditLog> getUserLogs(String userId);
    byte[] exportUserData(String userId);
    void importUserData(byte[] data);
    void updateUser(User user);
    void deleteUser(String userId);
}
```

<details>
<summary>参考答案</summary>

**判断**: 违反了接口隔离原则

**理由**:

1. **接口过于庞大**
   - 包含了4个不同的职责：用户管理、权限管理、审计日志、数据导出
   - 客户端很可能只需要其中一部分功能

2. **客户端被迫依赖不需要的接口**
   - 只需要查询用户的客户端，也看到了删除、权限、审计等方法
   - 只需要管理权限的客户端，也依赖了用户CRUD方法

3. **违反单一职责**
   - 一个接口承担了多个职责
   - 修改其中一个职责，影响所有客户端

**重构方案**:

```java
// 按功能拆分接口

// 用户基本操作
interface UserManagement {
    void createUser(User user);
    void updateUser(User user);
    void deleteUser(String userId);
    User getUser(String userId);
}

// 权限管理
interface RoleManagement {
    void assignRole(String userId, String role);
    void revokeRole(String userId, String role);
    Set<String> getUserRoles(String userId);
}

// 审计日志
interface AuditService {
    void logUserAction(String userId, String action);
    List<AuditLog> getUserLogs(String userId);
}

// 数据导出
interface DataExportService {
    byte[] exportUserData(String userId);
    void importUserData(byte[] data);
}

// 客户端只依赖需要的接口
class UserQueryClient {
    private UserManagement userManagement;  // 只依赖用户管理
    
    void displayUser(String userId) {
        User user = userManagement.getUser(userId);
        System.out.println(user);
    }
}

class AdminClient {
    private UserManagement userManagement;
    private RoleManagement roleManagement;
    private AuditService auditService;
    
    // 管理员需要多个功能，组合多个接口
}
```

**重构优势**:
- ✅ 接口职责单一，易于理解
- ✅ 客户端只依赖需要的接口
- ✅ 接口变化影响范围小
- ✅ 易于测试和维护
</details>

---

### 5. 设计符合 ISP 的接口

设计一个文档处理系统，需要支持以下功能：
- 可读（查看文档内容）
- 可写（创建、修改文档）
- 可审批（审批文档）
- 可归档（归档和恢复文档）

不同用户有不同权限：
- 普通用户：只能读
- 作者：可读、可写
- 审批者：可读、可审批
- 管理员：所有权限

请设计接口结构，确保符合接口隔离原则。

<details>
<summary>参考答案</summary>

**设计方案**:

```java
// ========== 接口层：按功能拆分 ==========

/**
 * Readable - 可读接口
 */
interface Readable {
    Document read(String docId);
    List<Document> search(String keyword);
}

/**
 * Writable - 可写接口
 */
interface Writable {
    void create(Document doc);
    void update(Document doc);
    void delete(String docId);
}

/**
 * Approvable - 可审批接口
 */
interface Approvable {
    void approve(String docId, String approverId);
    void reject(String docId, String reason);
    ApprovalStatus getApprovalStatus(String docId);
}

/**
 * Archivable - 可归档接口
 */
interface Archivable {
    void archive(String docId);
    void restore(String docId);
    List<Document> getArchivedDocs();
}

// ========== 用户类型：按需实现接口 ==========

/**
 * NormalUser - 普通用户（只读）
 */
class NormalUser implements Readable {
    public Document read(String docId) { /* 实现 */ }
    public List<Document> search(String keyword) { /* 实现 */ }
}

/**
 * Author - 作者（可读、可写）
 */
class Author implements Readable, Writable {
    public Document read(String docId) { /* 实现 */ }
    public List<Document> search(String keyword) { /* 实现 */ }
    public void create(Document doc) { /* 实现 */ }
    public void update(Document doc) { /* 实现 */ }
    public void delete(String docId) { /* 实现 */ }
}

/**
 * Approver - 审批者（可读、可审批）
 */
class Approver implements Readable, Approvable {
    public Document read(String docId) { /* 实现 */ }
    public List<Document> search(String keyword) { /* 实现 */ }
    public void approve(String docId, String approverId) { /* 实现 */ }
    public void reject(String docId, String reason) { /* 实现 */ }
    public ApprovalStatus getApprovalStatus(String docId) { /* 实现 */ }
}

/**
 * Admin - 管理员（所有权限）
 */
class Admin implements Readable, Writable, Approvable, Archivable {
    // 实现所有接口的方法
}

// ========== 客户端代码：只依赖需要的接口 ==========

/**
 * DocumentViewer - 文档查看器
 * 只依赖 Readable 接口
 */
class DocumentViewer {
    private Readable readable;
    
    public DocumentViewer(Readable readable) {
        this.readable = readable;
    }
    
    public void display(String docId) {
        Document doc = readable.read(docId);
        System.out.println(doc.getContent());
    }
}

/**
 * DocumentEditor - 文档编辑器
 * 依赖 Readable 和 Writable 接口
 */
class DocumentEditor {
    private Readable readable;
    private Writable writable;
    
    public DocumentEditor(Readable readable, Writable writable) {
        this.readable = readable;
        this.writable = writable;
    }
    
    public void edit(String docId, String newContent) {
        Document doc = readable.read(docId);
        doc.setContent(newContent);
        writable.update(doc);
    }
}
```

**设计要点**:

1. **接口隔离**
   - 按功能拆分成4个独立接口
   - 每个接口职责单一

2. **按需实现**
   - 普通用户只实现 Readable
   - 作者实现 Readable + Writable
   - 管理员实现所有接口

3. **客户端隔离**
   - 查看器只依赖 Readable
   - 编辑器依赖 Readable + Writable
   - 不依赖不需要的接口

4. **灵活组合**
   - 通过实现多个接口来组合功能
   - 易于扩展新的权限类型

5. **符合原则**
   - ✅ 符合接口隔离原则
   - ✅ 符合单一职责原则
   - ✅ 符合里氏替换原则
   - ✅ 符合开闭原则
</details>

---

## 三、综合题

### 6. ISP vs SRP vs LSP

请说明接口隔离原则（ISP）、单一职责原则（SRP）、里氏替换原则（LSP）之间的区别和联系。

<details>
<summary>参考答案</summary>

**核心区别**:

| 原则 | 关注点 | 核心思想 | 层次 |
|-----|--------|---------|------|
| **SRP** | 职责划分 | 一个类只做一件事 | 类级别 |
| **ISP** | 接口设计 | 客户端不依赖不需要的接口 | 接口级别 |
| **LSP** | 继承关系 | 子类必须能替换父类 | 继承级别 |

**详细关系**:

### 1. ISP 和 SRP 的关系

**相同点**：
- 都强调"单一"（SRP强调类职责单一，ISP强调接口职责单一）
- 都追求高内聚、低耦合

**不同点**：
- **SRP**：关注类的职责，一个类只做一件事
- **ISP**：关注接口的设计，接口应该按客户端需求拆分

**示例**：
```java
// SRP：职责分离
class UserService { }      // 用户管理
class RoleService { }      // 权限管理
class AuditService { }     // 审计日志

// ISP：接口分离
interface UserManagement { }
interface RoleManagement { }
interface AuditLog { }

// 联系：SRP帮助识别职责，ISP帮助设计接口
```

### 2. ISP 和 LSP 的关系

**ISP 有助于避免违反 LSP**：

```java
// 违反 ISP → 容易违反 LSP
interface MultiFunctionDevice {
    void print();
    void scan();
    void fax();
}

class SimplePrinter implements MultiFunctionDevice {
    void print() { /* 正常 */ }
    void scan() { throw new UnsupportedOperationException(); }  // 违反 LSP
    void fax() { throw new UnsupportedOperationException(); }   // 违反 LSP
}

// 符合 ISP → 自然符合 LSP
interface Printer {
    void print();
}

class SimplePrinter implements Printer {
    void print() { /* 正常 */ }  // 所有方法都能正常工作，符合 LSP
}
```

**原因**：
- ISP 避免了"接口污染"
- 实现类不会被迫实现不需要的方法
- 不会出现"抛异常"的方法
- 自然符合 LSP（子类能正确替换父类）

### 3. 三者的协作关系

```
SRP（职责单一）
    ↓
    ↓ 识别职责边界
    ↓
ISP（接口隔离）
    ↓
    ↓ 避免接口污染
    ↓
LSP（正确替换）
    ↓
    ↓ 多态有效
    ↓
OCP（可扩展）
```

**完整示例**：

```java
// === SRP：职责分离 ===
class UserService { }
class RoleService { }

// === ISP：接口隔离 ===
interface Readable {
    User getUser(String id);
}

interface Writable {
    void updateUser(User user);
}

// === LSP：正确实现 ===
class UserServiceImpl implements Readable, Writable {
    public User getUser(String id) { /* 正常实现 */ }
    public void updateUser(User user) { /* 正常实现 */ }
    // 所有方法都能正常工作，符合 LSP
}

// === OCP：易于扩展 ===
class AdminService implements Readable, Writable {
    // 新增实现，不修改现有代码
}
```

**总结**：
- **SRP** 提供职责划分的思路（基础）
- **ISP** 指导接口设计（桥梁）
- **LSP** 保证多态正确性（保障）
- 三者共同支持 **OCP**（目标）

</details>

---

## 总分统计

- **选择题**（1-3题）：每题 15 分，共 45 分
- **代码分析题**（4-5题）：每题 20 分，共 40 分
- **综合题**（6题）：15 分

**总分**: 100 分  
**及格线**: 80 分

---

## 学习建议

- ✅ 如果得分 ≥ 80分：恭喜！可以继续学习下一个原则
- ⚠️ 如果得分 60-79分：重新阅读 doc_01.md，重点理解"胖接口"问题
- ❌ 如果得分 < 60分：建议再运行一次 demo 代码，对比胖接口和小接口的区别

**核心要记住**：
1. **客户端不应该依赖它不使用的接口**
2. **接口应该小而专注**：按客户端需求设计
3. **避免胖接口**：不要强迫实现不需要的方法
4. **灵活组合**：通过实现多个接口来组合功能

**实践口诀**：接口不要贪大全，客户需要啥给啥，胖接口拆成小块，组合使用更灵活。
