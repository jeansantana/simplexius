package br.com.simplexius.model;

import java.math.BigInteger;

public class Fraction {

	private BigInteger numerator;
	private BigInteger denominator;

	public Fraction() {
		this.numerator = new BigInteger("0");
        this.denominator = new BigInteger("1");
	}

    public Fraction(BigInteger value) {
        this.denominator = new BigInteger("1");
        this.numerator = value;
    }

    public Fraction(String value) {
        this.denominator = new BigInteger("1");
        this.numerator = new BigInteger(value);
    }

	public Fraction(String numerator, String denominator) {
        //System.out.println(numerator + "/" + denominator);
        this.numerator = new BigInteger(numerator);
		if (denominator.compareTo("0") == 0) {
			//throw an excption here
			//throw new ArithmeticException();?
			System.err.println("Division by zero");
		}
		this.denominator = new BigInteger(denominator);

        this.simplify();
	}

    private void fixFraction() {
        if (this.isSignal() && this.denominator.signum() == -1) {
            this.denominator = this.denominator.negate();
            this.numerator = this.numerator.negate();
        }
    }
	
	public Fraction(BigInteger numerator, BigInteger denominator) {
		this.numerator = numerator;
		this.denominator = denominator;
        /*this.fixFraction();
        this.simplify();*/
	}

    public void setFraction(BigInteger numerator, BigInteger denominator) {
        this.setNumerator(numerator);
        this.setDenominator(denominator);
//        this.simplify();
    }

    public void setFraction(Fraction other) {
        this.numerator = other.getNumerator();
        this.denominator = other.getDenominator();
//        this.simplify();
    }

	public BigInteger getNumerator() {
		return numerator;
	}

	public void setNumerator(BigInteger numerator) {
		this.numerator = numerator;
	}

	public BigInteger getDenominator() {
		return denominator;
	}

	public void setDenominator(BigInteger denominator) {
		this.denominator = denominator;
	}
	
	// returns true if is negative, false case positive
    public boolean isSignal() {
        return this.numerator.signum() == -1 || this.denominator.signum() == -1;
    }
    
    public Fraction multip(Fraction other) {

        BigInteger denominator = this.getDenominator().multiply(other.getDenominator());
        BigInteger numerator = this.getNumerator().multiply(other.getNumerator());
        Fraction f = new Fraction(numerator, denominator);
        f.simplify();
        return f;
    }

    public Fraction div(Fraction other) {

    	BigInteger denominator = this.getDenominator().multiply(other.getNumerator());
    	BigInteger numerator = this.getNumerator().multiply(other.getDenominator());

        Fraction f = new Fraction(numerator, denominator);
        f.simplify();
        return f;
    }

    public double getValue() {
        return (double) numerator.intValue() / denominator.intValue();
    }

    private void adjust(Fraction other) {
        if ( !this.denominator.equals(other.getDenominator()) ) {
            BigInteger d1 = this.denominator, d2 = other.getDenominator();

            this.numerator = this.numerator.multiply(d2);
            this.denominator = this.denominator.multiply(d2);

            other.setNumerator(other.numerator.multiply(d1));
            other.setDenominator(other.denominator.multiply(d1));
        }
    }

    public Fraction sum(Fraction other) {
        this.adjust(other);
        BigInteger numerator = this.numerator.add(other.getNumerator());

        Fraction f = new Fraction(numerator, this.denominator);
        this.simplify();
        f.simplify();
        return f;
    }

    public String print() {
        return this.numerator.toString() +"/"+ this.denominator.toString();
    }

    public Fraction subtract(Fraction other) {
        //System.out.println(print() + " + " + other.print() + " = ");
        //other.simplify();
        //this.simplify();
        this.adjust(other);
        //System.out.println("ADJ: " + print() + " + " + other.print() + " = ");
        BigInteger numerator = this.numerator.subtract(other.getNumerator());
        Fraction f = new Fraction(numerator, this.denominator);
        f.simplify();
        //System.out.println(f);
        return f;
    }

    private BigInteger gcd(BigInteger a, BigInteger b) {
        if (b.equals(new BigInteger("0"))) {
            return a;
        } else {
            return gcd(b, a.mod(b));
        }
    }

    public void simplify() {
        this.fixFraction();
        BigInteger mdc = gcd(this.numerator, this.denominator);
        this.numerator = this.numerator.divide(mdc);
        this.denominator = this.denominator.divide(mdc);
    }

	@Override
    public String toString() {
        if (this.getNumerator().intValue() % this.getDenominator().intValue() == 0) {
            return this.getNumerator().intValue() / this.getDenominator().intValue() + "";
        }
        return numerator + "/" + denominator;
    }
	
}
