package com.qx.sandboxLab.ISP;

import lombok.Data;
import java.util.List;

@Data
class Document {
    private String docId;
    private String context;
}

@Data
class ApprovalStatus {
    private String approverId;
    private String approvalStatus;  // 修正：改为小写开头，符合Java命名规范
}

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

/**
 * 标准用户 - 只有读权限
 */
class StandardUser implements Readable {
    @Override
    public Document read(String docId) {
        // 实现：根据docId查询文档
        return null;
    }

    @Override
    public List<Document> search(String keyword) {
        // 实现：根据关键字搜索文档
        return null;
    }
}

/**
 * 编辑者 - 读和写权限
 */
class Editor implements Readable, Writable {
    @Override
    public Document read(String docId) {
        // 实现：根据docId查询文档
        return null;
    }

    @Override
    public List<Document> search(String keyword) {
        // 实现：根据关键字搜索文档
        return null;
    }

    @Override
    public void create(Document doc) {
        // 实现：创建新文档
    }

    @Override
    public void update(Document doc) {
        // 实现：更新文档
    }

    @Override
    public void delete(String docId) {
        // 实现：删除文档
    }
}

/**
 * 审批者 - 读和审批权限
 */
class Approver implements Readable, Approvable {
    @Override
    public Document read(String docId) {
        // 实现：根据docId查询文档
        return null;
    }

    @Override
    public List<Document> search(String keyword) {
        // 实现：根据关键字搜索文档
        return null;
    }

    @Override
    public void approve(String docId, String approverId) {
        // 实现：审批通过文档
    }

    @Override
    public void reject(String docId, String reason) {
        // 实现：驳回文档，并记录原因
    }

    @Override
    public ApprovalStatus getApprovalStatus(String docId) {
        // 实现：查询文档的审批状态
        return null;
    }
}

/**
 * 管理员 - 拥有所有权限
 */
class Admin implements Readable, Writable, Approvable, Archivable {
    @Override
    public Document read(String docId) {
        // 实现：根据docId查询文档
        return null;
    }

    @Override
    public List<Document> search(String keyword) {
        // 实现：根据关键字搜索文档
        return null;
    }

    @Override
    public void create(Document doc) {
        // 实现：创建新文档
    }

    @Override
    public void update(Document doc) {
        // 实现：更新文档
    }

    @Override
    public void delete(String docId) {
        // 实现：删除文档
    }

    @Override
    public void approve(String docId, String approverId) {
        // 实现：审批通过文档
    }

    @Override
    public void reject(String docId, String reason) {
        // 实现：驳回文档，并记录原因
    }

    @Override
    public ApprovalStatus getApprovalStatus(String docId) {
        // 实现：查询文档的审批状态
        return null;
    }

    @Override
    public void archive(String docId) {
        // 实现：归档文档
    }

    @Override
    public void restore(String docId) {
        // 实现：恢复已归档的文档
    }

    @Override
    public List<Document> getArchivedDocs() {
        // 实现：获取所有已归档的文档列表
        return null;
    }
}