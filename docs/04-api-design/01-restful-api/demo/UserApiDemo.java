import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * RESTful API 完整示例：用户管理系统
 *
 * 演示内容：
 * 1. 资源命名（使用名词复数）
 * 2. HTTP方法语义（GET/POST/PUT/PATCH/DELETE）
 * 3. HTTP状态码使用
 * 4. 分页、过滤、排序
 * 5. 错误处理
 * 6. 响应格式设计
 */
public class UserApiDemo {

    public static void main(String[] args) {
        UserApiController controller = new UserApiController();

        System.out.println("=".repeat(80));
        System.out.println("RESTful API 演示：用户管理系统");
        System.out.println("=".repeat(80));
        System.out.println();

        // 1. 列出所有用户
        demo("1. 获取用户列表", () -> controller.listUsers(null, null, null, null));

        // 2. 获取单个用户
        demo("2. 获取单个用户", () -> controller.getUser(1L));

        // 3. 获取不存在的用户
        demo("3. 获取不存在的用户（404示例）", () -> controller.getUser(999L));

        // 4. 创建用户
        demo("4. 创建新用户", () -> {
            User newUser = new User(null, "王五", "wangwu@example.com", "active");
            return controller.createUser(newUser);
        });

        // 5. 创建用户（邮箱已存在）
        demo("5. 创建用户（邮箱冲突，409示例）", () -> {
            User duplicateUser = new User(null, "赵六", "zhangsan@example.com", "active");
            return controller.createUser(duplicateUser);
        });

        // 6. 完整更新用户（PUT）
        demo("6. 完整更新用户（PUT）", () -> {
            User updateUser = new User(1L, "张三丰", "zhangsanfeng@example.com", "active");
            return controller.updateUser(1L, updateUser);
        });

        // 7. 部分更新用户（PATCH）
        demo("7. 部分更新用户（PATCH）", () -> {
            Map<String, Object> updates = new HashMap<>();
            updates.put("email", "newemail@example.com");
            return controller.patchUser(1L, updates);
        });

        // 8. 删除用户
        demo("8. 删除用户", () -> controller.deleteUser(3L));

        // 9. 再次删除（幂等性）
        demo("9. 再次删除同一用户（幂等性，404示例）", () -> controller.deleteUser(3L));

        // 10. 分页查询
        demo("10. 分页查询（第1页，每页2条）", () -> controller.listUsers(1, 2, null, null));

        // 11. 过滤查询
        demo("11. 过滤查询（status=active）", () -> controller.listUsers(null, null, "active", null));

        // 12. 排序查询
        demo("12. 排序查询（按ID降序）", () -> controller.listUsers(null, null, null, "-id"));

        // 13. 组合查询
        demo("13. 组合查询（过滤+排序+分页）", () ->
            controller.listUsers(1, 2, "active", "-id"));
    }

    private static void demo(String title, DemoAction action) {
        System.out.println("【" + title + "】");
        System.out.println("-".repeat(80));
        try {
            ApiResponse response = action.execute();
            response.print();
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
        System.out.println();
    }

    @FunctionalInterface
    interface DemoAction {
        ApiResponse execute();
    }
}

/**
 * 模拟的API控制器
 */
class UserApiController {
    private Map<Long, User> userDatabase = new HashMap<>();
    private Long nextId = 4L;

    public UserApiController() {
        // 初始化测试数据
        userDatabase.put(1L, new User(1L, "张三", "zhangsan@example.com", "active"));
        userDatabase.put(2L, new User(2L, "李四", "lisi@example.com", "active"));
        userDatabase.put(3L, new User(3L, "王五", "wangwu@example.com", "inactive"));
    }

    /**
     * GET /users?page=1&size=20&status=active&sort=-id
     */
    public ApiResponse listUsers(Integer page, Integer size, String status, String sort) {
        String path = "/users";
        List<String> params = new ArrayList<>();
        if (page != null) params.add("page=" + page);
        if (size != null) params.add("size=" + size);
        if (status != null) params.add("status=" + status);
        if (sort != null) params.add("sort=" + sort);
        if (!params.isEmpty()) path += "?" + String.join("&", params);

        // 过滤
        List<User> users = new ArrayList<>(userDatabase.values());
        if (status != null) {
            users = users.stream()
                .filter(u -> u.status.equals(status))
                .collect(Collectors.toList());
        }

        // 排序
        if (sort != null) {
            boolean desc = sort.startsWith("-");
            String field = desc ? sort.substring(1) : sort;
            Comparator<User> comparator = (u1, u2) -> {
                if ("id".equals(field)) {
                    return u1.id.compareTo(u2.id);
                }
                return u1.name.compareTo(u2.name);
            };
            if (desc) comparator = comparator.reversed();
            users.sort(comparator);
        }

        // 分页
        int totalCount = users.size();
        if (page != null && size != null) {
            int start = (page - 1) * size;
            int end = Math.min(start + size, users.size());
            if (start < users.size()) {
                users = users.subList(start, end);
            } else {
                users = Collections.emptyList();
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("data", users);
        if (page != null && size != null) {
            Map<String, Object> pagination = new HashMap<>();
            pagination.put("page", page);
            pagination.put("size", size);
            pagination.put("totalCount", totalCount);
            pagination.put("totalPages", (int) Math.ceil((double) totalCount / size));
            response.put("pagination", pagination);
        }

        return new ApiResponse("GET", path, null, 200, response);
    }

    /**
     * GET /users/{id}
     */
    public ApiResponse getUser(Long id) {
        String path = "/users/" + id;
        User user = userDatabase.get(id);

        if (user == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 40400);
            error.put("message", "用户不存在");
            error.put("path", path);
            return new ApiResponse("GET", path, null, 404, error);
        }

        return new ApiResponse("GET", path, null, 200, user);
    }

    /**
     * POST /users
     */
    public ApiResponse createUser(User user) {
        String path = "/users";

        // 验证邮箱是否已存在
        boolean emailExists = userDatabase.values().stream()
            .anyMatch(u -> u.email.equals(user.email));

        if (emailExists) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 40900);
            error.put("message", "邮箱已被注册");
            error.put("field", "email");
            error.put("value", user.email);
            return new ApiResponse("POST", path, user, 409, error);
        }

        // 创建用户
        user.id = nextId++;
        user.createdAt = LocalDateTime.now();
        userDatabase.put(user.id, user);

        Map<String, Object> response = new HashMap<>();
        response.put("data", user);

        return new ApiResponse("POST", path, user, 201, response)
            .withHeader("Location", "/users/" + user.id);
    }

    /**
     * PUT /users/{id}
     */
    public ApiResponse updateUser(Long id, User user) {
        String path = "/users/" + id;

        if (!userDatabase.containsKey(id)) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 40400);
            error.put("message", "用户不存在");
            return new ApiResponse("PUT", path, user, 404, error);
        }

        user.id = id;
        user.updatedAt = LocalDateTime.now();
        userDatabase.put(id, user);

        Map<String, Object> response = new HashMap<>();
        response.put("data", user);

        return new ApiResponse("PUT", path, user, 200, response);
    }

    /**
     * PATCH /users/{id}
     */
    public ApiResponse patchUser(Long id, Map<String, Object> updates) {
        String path = "/users/" + id;
        User user = userDatabase.get(id);

        if (user == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 40400);
            error.put("message", "用户不存在");
            return new ApiResponse("PATCH", path, updates, 404, error);
        }

        // 部分更新
        if (updates.containsKey("name")) user.name = (String) updates.get("name");
        if (updates.containsKey("email")) user.email = (String) updates.get("email");
        if (updates.containsKey("status")) user.status = (String) updates.get("status");
        user.updatedAt = LocalDateTime.now();

        Map<String, Object> response = new HashMap<>();
        response.put("data", user);

        return new ApiResponse("PATCH", path, updates, 200, response);
    }

    /**
     * DELETE /users/{id}
     */
    public ApiResponse deleteUser(Long id) {
        String path = "/users/" + id;

        if (!userDatabase.containsKey(id)) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 40400);
            error.put("message", "用户不存在");
            return new ApiResponse("DELETE", path, null, 404, error);
        }

        userDatabase.remove(id);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "用户已删除");
        response.put("deletedId", id);

        return new ApiResponse("DELETE", path, null, 204, null);
    }
}

/**
 * 用户实体
 */
class User {
    Long id;
    String name;
    String email;
    String status;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    public User(Long id, String name, String email, String status) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format(
            "{\"id\": %d, \"name\": \"%s\", \"email\": \"%s\", \"status\": \"%s\", \"createdAt\": \"%s\"%s}",
            id, name, email, status,
            createdAt.format(formatter),
            updatedAt != null ? ", \"updatedAt\": \"" + updatedAt.format(formatter) + "\"" : ""
        );
    }
}

/**
 * API响应封装
 */
class ApiResponse {
    String method;
    String path;
    Object requestBody;
    int statusCode;
    Object responseBody;
    Map<String, String> headers = new HashMap<>();

    public ApiResponse(String method, String path, Object requestBody,
                       int statusCode, Object responseBody) {
        this.method = method;
        this.path = path;
        this.requestBody = requestBody;
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public ApiResponse withHeader(String key, String value) {
        headers.put(key, value);
        return this;
    }

    public void print() {
        System.out.println("请求：");
        System.out.println("  " + method + " " + path);
        if (requestBody != null) {
            System.out.println("  Body: " + formatJson(requestBody));
        }
        System.out.println();

        System.out.println("响应：");
        System.out.println("  HTTP/1.1 " + statusCode + " " + getStatusText(statusCode));
        if (!headers.isEmpty()) {
            headers.forEach((k, v) -> System.out.println("  " + k + ": " + v));
        }
        if (responseBody != null) {
            System.out.println("  Body: " + formatJson(responseBody));
        }
    }

    private String getStatusText(int code) {
        switch (code) {
            case 200: return "OK";
            case 201: return "Created";
            case 204: return "No Content";
            case 400: return "Bad Request";
            case 404: return "Not Found";
            case 409: return "Conflict";
            case 422: return "Unprocessable Entity";
            case 500: return "Internal Server Error";
            default: return "Unknown";
        }
    }

    private String formatJson(Object obj) {
        if (obj instanceof List) {
            List<?> list = (List<?>) obj;
            if (list.isEmpty()) return "[]";
            return "[\n    " + list.stream()
                .map(Object::toString)
                .collect(Collectors.joining(",\n    ")) + "\n  ]";
        }
        if (obj instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) obj;
            StringBuilder sb = new StringBuilder("{\n");
            map.forEach((k, v) -> {
                sb.append("    \"").append(k).append("\": ");
                if (v instanceof String) {
                    sb.append("\"").append(v).append("\"");
                } else if (v instanceof List) {
                    sb.append(formatJson(v).replace("\n", "\n    "));
                } else if (v instanceof Map) {
                    sb.append(formatJson(v).replace("\n", "\n    "));
                } else {
                    sb.append(v);
                }
                sb.append(",\n");
            });
            sb.setLength(sb.length() - 2); // 移除最后的逗号和换行
            sb.append("\n  }");
            return sb.toString();
        }
        return obj.toString();
    }
}
