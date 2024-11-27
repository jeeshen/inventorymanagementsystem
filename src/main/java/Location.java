public class Location {
    private String locationID;
    private String warehouseID;
    private int rackID;
    private String rowID;

    public static int locationCount = 0;

    public Location(String warehouseID, int rackID, String rowID) {
        this.warehouseID = warehouseID;
        this.rackID = rackID;
        this.rowID = rowID;
        generateLocationID();
    }

    public Location() {
        this("",0,"");
    }

    //getter
    public String getLocationID() {
        return this.locationID;
    }

    public String getWarehouseID() {
        return this.warehouseID;
    }

    public int getRackID() {
        return this.rackID;
    }

    public String getRowID() {
        return this.rowID;
    }

    //setter
    public void setWarehouseID(String warehouseID) {
        this.warehouseID = warehouseID;
    }

    public void setRackID(int rackID) {
        this.rackID = rackID;
    }

    public void setRowID(String rowID) {
        this.rowID = rowID;
    }

    //other methods
    public void generateLocationID() {
        String prefix = "L";
        this.locationID = String.format("%s%04d",prefix,locationCount+1);
    }

    public void loadLocation() {
        String[] rowName = {"A","B","C","D","E","F","G","H"}; //f - inbound, g - outbound, h - return
        for (int i =0; i < 5; i++) {
            for (int j=1;j<=10;j++) {
                Main.locationList[locationCount] = new Location("W0001",j,rowName[i]);
                locationCount++;
            }
        }

        //inbound
        Main.locationList[locationCount] = new Location("W0001",1,rowName[5]);
        locationCount++;

        //outbound
        Main.locationList[locationCount] = new Location("W0001",1,rowName[6]);
        locationCount++;

        //return
        Main.locationList[locationCount] = new Location("W0001",1,rowName[7]);
        locationCount++;
    }

    @Override
    public String toString() {
        return "Location{"+
                "Location ID: "+this.locationID+
                ",Warehouse ID: "+this.warehouseID+
                ",Rack ID: "+this.rackID+
                ",Row ID: "+this.rowID+
                "}";
    }
}
