package org.salaboy.streams.model;

public class ComplexDataStructure {

    private String id;
    private Long longNumber;
    private String someOtherValue;

    public ComplexDataStructure() {
    }

    public ComplexDataStructure(String id,
                                Long longNumber,
                                String someOtherValue) {
        this.id = id;
        this.longNumber = longNumber;
        this.someOtherValue = someOtherValue;
    }

    public String getId() {
        return id;
    }

    public Long getLongNumber() {
        return longNumber;
    }

    public String getSomeOtherValue() {
        return someOtherValue;
    }
}
