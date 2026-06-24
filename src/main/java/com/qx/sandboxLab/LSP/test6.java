package com.qx.sandboxLab.LSP;

import lombok.Setter;


interface Shape {
    double Perimeter();
    double Area();

    // 以下不做实现，都一样的
    // void resize(double factor);
    // String getType();
}

@Setter
class Rectangle implements Shape {
    private double height;
    private double weight;

    public Rectangle(double height, double weight) {
        this.height = height;
        this.weight = weight;
    }

    @Override
    public double Perimeter() {
        return 2 * (height + weight);
    }

    @Override
    public double Area() {
        return (height * weight);
    }
}

@Setter
class Square implements Shape {
    private double side;

    public Square(double side) {
        this.side = side;
    }

    @Override
    public double Perimeter() {
        return 4 * side;
    }

    @Override
    public double Area() {
        return side * side;
    }
}

@Setter
class Circle implements Shape {
    private double radius;

    private double pi = 3.141592653589793238462643383279;

    public Circle(double radius) {
        this.radius = radius;
    }

    @Override
    public double Perimeter() {
        return 2 * pi * radius;
    }

    @Override
    public double Area() {
        return pi * (radius * radius);
    }
}

@Setter
class shapeService {
    private Shape shape;

    public shapeService(Shape shape) {
        this.shape = shape;
    }

    public double Perimeter() {
        return shape.Perimeter();
    }

    public double Area() {
        return shape.Area();
    }
}
