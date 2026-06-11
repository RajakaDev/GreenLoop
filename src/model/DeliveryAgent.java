package model;

public class DeliveryAgent {
    private int agentId;
    private String name;
    private String email;
    private String phone;
    private String vehicleNo;
    private String vehicleType;

    public DeliveryAgent(int agentId, String name, String email, String phone, String vehicleNo, String vehicleType) {
        this.agentId = agentId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.vehicleNo = vehicleNo;
        this.vehicleType = vehicleType;
    }

    public DeliveryAgent(String name, String email, String phone, String vehicleNo, String vehicleType) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.vehicleNo = vehicleNo;
        this.vehicleType = vehicleType;
    }

    public int getAgentId() { return agentId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getVehicleNo() { return vehicleNo; }
    public String getVehicleType() { return vehicleType; }
}