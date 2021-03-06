package org.latinolib;

/**
 * Author saxo
 */
public interface VectorEntry extends Comparable<VectorEntry>
{
    int getIndex();
    double getData();
    void setData(double value);
}
