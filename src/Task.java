public abstract class Task {
    private String name;
    private Integer id;
    private String description;
    private String status = "NEW";


    Task(String name, String description){
        this.name = name;
        this.description = description;
        this.id = this.hashCode();
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}


