class ReportGenerater {
    // 生成报表内容
    public String generate(Report report) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ").append(report.getTitle()).append(" ===\n");
        for (String item : report.getData()) {
            sb.append("- ").append(item).append("\n");
        }
        return sb.toString();
    }
}

class ReportFileSave {

    // 保存到文件
    public void saveToFile(String content, String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(content);
            System.out.println("Report saved to " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class Report {
    private String title;
    private List<String> data;

    public Report(String title, List<String> data) {
        this.title = title;
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }
}