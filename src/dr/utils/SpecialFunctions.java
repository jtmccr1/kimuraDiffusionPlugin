package dr.utils;
//Adapted from :
/*                                                      hyp2f1.c
 *
 *      Gauss hypergeometric function   F
 *                                     2 1
 *
 *
 * SYNOPSIS:
 *
 * double a, b, c, x, y, hyp2f1();
 *
 * y = hyp2f1( a, b, c, x );
 *
 *
 * DESCRIPTION:
 *
 *
 *  hyp2f1( a, b, c, x )  =   F ( a, b; c; x )
 *                           2 1
 *
 *           inf.
 *            -   a(a+1)...(a+k) b(b+1)...(b+k)   k+1
 *   =  1 +   >   -----------------------------  x   .
 *            -         c(c+1)...(c+k) (k+1)!
 *          k = 0
 *
 *  Cases addressed are
 *      Tests and escapes for negative integer a, b, or c
 *      Linear transformation if c - a or c - b negative integer
 *      Special case c = a or c = b
 *      Linear transformation for  x near +1
 *      Transformation for x < -0.5
 *      Psi function expansion if x > 0.5 and c - a - b integer
 *      Conditionally, a recurrence on c to make c-a-b > 0
 *
 *      x < -1  AMS 15.3.7 transformation applied (Travis Oliphant)
 *         valid for b,a,c,(b-a) != integer and (c-a),(c-b) != negative integer
 *
 * x >= 1 is rejected (unless special cases are present)
 *
 * The parameters a, b, c are considered to be integer
 * valued if they are within 1.0e-14 of the nearest integer
 * (1.0e-13 for IEEE arithmetic).
 *
 * ACCURACY:
 *
 *
 *               Relative error (-1 < x < 1):
 * arithmetic   domain     # trials      peak         rms
 *    IEEE      -1,7        230000      1.2e-11     5.2e-14
 *
 * Several special cases also tested with a, b, c in
 * the range -7 to 7.
 *
 * ERROR MESSAGES:
 *
 * A "partial loss of precision" message is printed if
 * the internally estimated relative error exceeds 1^-12.
 * A "singularity" message is printed on overflow or
 * in cases not addressed (such as x < -1).
 */

/*                                                      hyp2f1  */


/*
 * Cephes Math Library Release 2.8:  June, 2000
 * Copyright 1984, 1987, 1992, 2000 by Stephen L. Moshier
 */


import org.apache.commons.math.special.Gamma;

import static dr.math.functionEval.GammaFunction.gamma;

public class SpecialFunctions {
    private static final double EPS =  1.0e-13;
    private static final double EPS2  = 1.0e-10;
    private static final double ETHRESH  = 1.0e-12;
    private static final double MAX_ITERATIONS  = 10000;
    private static final double MACHEP =  1.11022302462515654042E-16; // IEEE

    private static double err;
    private static double err1;
    private static double loss;

  public static  double hyp2f1(double a, double b, double c, double x) {
        double d, d1, d2, e;
        double p, q, r, s, y = 0, ax;
        double ia, ib, ic, id;
        double t1;
        int i, aid;
        boolean neg_int_a = false, neg_int_b = false;
        boolean neg_int_ca_or_cb = false;

        err = 0.0;
        err1 =0.0;
        loss= 0.0;
        ax = Math.abs(x);
        s = 1.0 - x;
        ia = Math.round(a);		/* nearest integer to a */
        ib = Math.round(b);

        if (x == 0.0) {
            return 1.0;
        }

        d = c - a - b;
        id = Math.round(d);

        if ((a == 0 || b == 0) && c != 0) {
            return 1.0;
        }

        if (a <= 0 && Math.abs(a - ia) < EPS) {	/* a is a negative integer */
            neg_int_a = true;
        }

        if (b <= 0 && Math.abs(b - ib) < EPS) {	/* b is a negative integer */
            neg_int_b = true;
        }

        if (d <= -1 && !(Math.abs(d - id) > EPS && s < 0)
                && !(neg_int_a || neg_int_b)) {
            return Math.pow(s, d) * hyp2f1(c - a, c - b, c, x);
        }
        if (d <= 0 && x == 1 && !(neg_int_a || neg_int_b)) {
            return hypdiv();
        }


        if (ax < 1.0 || x == -1.0) {
            /* 2F1(a,b;b;x) = (1-x)**(-a) */
            if (Math.abs(b - c) < EPS) {	/* b = c */
                if (neg_int_b) {
                    y = hyp2f1_neg_c_equal_bc(a, b, x);
                } else {
                    y = Math.pow(s, -a);	/* s to the -a power */
                }
                return hypdon(err, y);
            }
            if (Math.abs(a - c) < EPS) {	/* a = c */
                y = Math.pow(s, -b);	/* s to the -b power */
                return hypdon(err, y);
            }
        }


        if (c <= 0.0) {
            ic = Math.round(c);		/* nearest integer to c */
            if (Math.abs(c - ic) < EPS) {	/* c is a negative integer */
                /* check if termination before explosion */
                if (neg_int_a && (ia > ic)){
                    		y= hypok(a,b,c,x,err);

                }
                if (neg_int_b && (ib > ic)){
                   y= hypok(a,b,c,x,err);

                }
	    return hypdiv();
            }
        }

        if (neg_int_a || neg_int_b){/* function is a polynomial */
           y= hypok(a,b,c,x,err);
        }

        t1 = Math.abs(b - a);
        if (x < -2.0 && Math.abs(t1 - Math.round(t1)) > EPS) {
            /* This transform has a pole for b-a integer, and
             * may produce large cancellation errors for |1/x| close 1
             */
            p = hyp2f1(a, 1 - c + a, 1 - b + a, 1.0 / x);
            q = hyp2f1(b, 1 - c + b, 1 - a + b, 1.0 / x);
            p *= Math.pow(-x, -a);
            q *= Math.pow(-x, -b);
            t1 = gamma(c);
            s = t1 * gamma(b - a) / (gamma(b) * gamma(c - a));
            y = t1 * gamma(a - b) / (gamma(a) * gamma(c - b));
            return s * p + y * q;
        }
        else if (x < -1.0) {
            if (Math.abs(a) < Math.abs(b)) {
                return Math.pow(s, -a) * hyp2f1(a, c - b, c, x / (x - 1));
            }
            else {
                return Math.pow(s, -b) * hyp2f1(b, c - a, c, x / (x - 1));
            }
        }

        if (ax > 1.0){
            /* series diverges  */
            return hypdiv();
        }
        p = c - a;
        ia = Math.round(p);		/* nearest integer to c-a */
        if ((ia <= 0.0) && (Math.abs(p - ia) < EPS))	/* negative int c - a */
            neg_int_ca_or_cb = true;

        r = c - b;
        ib = Math.round(r);		/* nearest integer to c-b */
        if ((ib <= 0.0) && (Math.abs(r - ib) < EPS))	/* negative int c - b */
            neg_int_ca_or_cb = true;

        id = Math.round(d);		/* nearest integer to d */
        q = Math.abs(d - id);

        /* Thanks to Christian Burger <BURGER@DMRHRZ11.HRZ.Uni-Marburg.DE>
         * for reporting a bug here.  */
        if (Math.abs(ax - 1.0) < EPS) {	/* |x| == 1.0   */
            if (x > 0.0) {
                if (neg_int_ca_or_cb) {
                    if (d >= 0.0) {
		                y= hypf(s,d,a,b,c,x,err);
                    }
		else {
		    return hypdiv();
                    }
                }
                if (d <= 0.0){
                    return hypdiv();
                }
                y = gamma(c) * gamma(d) / (gamma(p) * gamma(r));
	    return hypdon(err,y);
            }
            if (d <= -1.0){
                return hypdiv();
            }
        }

        /* Conditionally make d > 0 by recurrence on c
         * AMS55 #15.2.27
         */
        if (d < 0.0) {
            /* Try the power series first */
            //TODO actually try it first
//            y = hyt2f1(a, b, c, x, err);
//            if (err < ETHRESH) {
//                return hypdon(err, y);
//            }
            /* Apply the recurrence if power series fails */
            err = 0.0;
            aid = (int) (2 - id);
            e = c + aid;
            d2 = hyp2f1(a, b, e, x);
            d1 = hyp2f1(a, b, e + 1.0, x);
            q = a + b + 1.0;
            for (i = 0; i < aid; i++) {
                r = e - 1.0;
                y = (e * (r - (2.0 * e - q) * x) * d2 +
                        (e - a) * (e - b) * x * d1) / (e * r * s);
                e = r;
                d1 = d2;
                d2 = y;
            }
	        return hypdon(err,y);
        }


        if (neg_int_ca_or_cb){
            y= hypf(s,d,a,b,c,x,err);
        }


        return hypdon(err,y);

        /* The alarm exit */

    }


    private static double hypdiv(){
        System.out.println("hyp2f1 underflow");
        return Double.NEGATIVE_INFINITY;
    }
    private static double  hypdon(double err, double y){
        if (err > ETHRESH) {
            System.out.println("hyp2f1 error loss");
            /*      printf( "Estimated err = %.2e\n", err ); */
        }
        return (y);
    }

    private static double hypok(double a,double b,double c,double x, double err){
        return hyt2f1(a, b, c, x, err);
    }
    /* The transformation for c-a or c-b negative integer
     * AMS55 #15.3.3
     */
    private static double hypf(double s, double d, double a, double b, double c, double x, double err){
        double y = Math.pow(s, d) * hys2f1(c - a, c - b, c, x, err);
        return hypdon(err,y);
    }


    /* Apply transformations for |x| near 1
     * then call the power series
     */
    static double hyt2f1(double a, double b, double c, double x, double err){
        double p, q, r, s, t, y, w, d;
        double ax, id, d1, d2, e, y1;
        int i, aid, sign;

        int ia, ib;
        boolean neg_int_a = false, neg_int_b = false;

        ia = (int) Math.round(a);
        ib = (int) Math.round(b);

        if (a <= 0 && Math.abs(a - ia) < EPS) {	/* a is a negative integer */
            neg_int_a = true;
        }

        if (b <= 0 && Math.abs(b - ib) < EPS) {	/* b is a negative integer */
            neg_int_b = true;
        }

        s = 1.0 - x;
        if (x < -0.5 && !(neg_int_a || neg_int_b)) {
            if (b > a) {
                y = Math.pow(s, -a) * hys2f1(a, c - b, c, -x / s, err);
            }

	else {
                y = Math.pow(s, -b) * hys2f1(c - a, b, c, -x / s, err);
            }

        loss = err;
	    return (y);
        }

        d = c - a - b;
        id = Math.round(d);		/* nearest integer to d */

        if (x > 0.9 && !(neg_int_a || neg_int_b)) {
            if (Math.abs(d - id) > EPS) {
                int sgngam = 1;//TODO

                /* test for integer c-a-b */
                /* Try the power series first */
                y = hys2f1(a, b, c, x, err);
                if (err < ETHRESH){
                    loss = err;
                    return (y);
                }
                throw new RuntimeException("Help still need this");
//                /* If power series fails, then apply AMS55 #15.3.6 */
//                q = hys2f1(a, b, 1.0 - d, s,err);
//                sign = 1;
//                w = lgam_sgn(d, sgngam);
//                sign *= sgngam;
//                w -= lgam_sgn(c-a, sgngam);
//                sign *= sgngam;
//                w -= lgam_sgn(c-b,sgngam);
//                sign *= sgngam;
//                q *= sign * Math.exp(w);
//                r = Math.pow(s, d) * hys2f1(c - a, c - b, d + 1.0, s, err1);
//                sign = 1;
//                w = lgam_sgn(-d, sgngam);
//                sign *= sgngam;
//                w -= lgam_sgn(a, sgngam);
//                sign *= sgngam;
//                w -= lgam_sgn(b, sgngam);
//                sign *= sgngam;
//                r *= sign * Math.exp(w);
//                y = q + r;
//
//                q = Math.abs(q);	/* estimate cancellation error */
//                r = Math.abs(r);
//                if (q > r)
//                    r = q;
//                err += err1 + (MACHEP * r) / y;
//
//                y *= gamma(c);
//                loss = err;
//                return (y);
            }
            else {
                /* Gamma.digamma function expansion, AMS55 #15.3.10, #15.3.11, #15.3.12
                 *
                 * Although AMS55 does not explicitly state it, this expansion fails
                 * for negative integer a or b, since the Gamma.digamma and Gamma functions
                 * involved have poles.
                 */

                if (id >= 0.0) {
                    e = d;
                    d1 = d;
                    d2 = 0.0;
                    aid = (int) id;
                }
                else {
                    e = -d;
                    d1 = 0.0;
                    d2 = d;
                    aid = (int) -id;
                }

                ax = Math.log(s);

                /* sum for t = 0 */
                y = Gamma.digamma(1.0) + Gamma.digamma(1.0 + e) - Gamma.digamma(a + d1) - Gamma.digamma(b + d1) - ax;
                y /= gamma(e + 1.0);

                p = (a + d1) * (b + d1) * s / gamma(e + 2.0);	/* Poch for t=1 */
                t = 1.0;
                do {
                    r = Gamma.digamma(1.0 + t) + Gamma.digamma(1.0 + t + e) - Gamma.digamma(a + t + d1)
                            - Gamma.digamma(b + t + d1) - ax;
                    q = p * r;
                    y += q;
                    p *= s * (a + t + d1) / (t + 1.0);
                    p *= (b + t + d1) / (t + 1.0 + e);
                    t += 1.0;
                    if (t > MAX_ITERATIONS) {	/* should never happen */
                        System.out.println("hyp2f1 converges too slowly");
		            loss = 1.0;
                        return Double.NaN;
                    }
                }
                while (y == 0 || Math.abs(q / y) > EPS);

                if (id == 0.0) {
                    y *= gamma(c) / (gamma(a) * gamma(b));
                    loss = err;
                    return (y);
                }

                y1 = 1.0;

                if (aid == 1){
//                    		goto nosum;
                    p = gamma(c);
                    y1 *= gamma(e) * p / (gamma(a + d1) * gamma(b + d1));

                    y *= p / (gamma(a + d2) * gamma(b + d2));
                    if ((aid & 1) != 0)
                        y = -y;

                    q = Math.pow(s, id);	/* s to the id power */
                    if (id > 0.0)
                        y *= q;
                    else
                        y1 *= q;

                    y += y1;
                }

                t = 0.0;
                p = 1.0;
                for (i = 1; i < aid; i++) {
                    r = 1.0 - e + t;
                    p *= s * (a + t + d2) * (b + t + d2) / r;
                    t += 1.0;
                    p /= t;
                    y1 += p;
                }

            }

        }
        /* Use defining power series if no special cases */
        y = hys2f1(a, b, c, x, err);
        return y;
    }





    /* Defining power series expansion of Gauss hypergeometric function */

    static double hys2f1(double a, double b, double c, double x, double loss){

        double f, g, h, k, m, s, u, umax;
        int i;
        int ib;
        boolean intflag = false;

        if (Math.abs(b) > Math.abs(a)) {
            /* Ensure that |a| > |b| ... */
            f = b;
            b = a;
            a = f;
        }

        ib = (int) Math.round(b);

        if (Math.abs(b - ib) < EPS && ib <= 0 && Math.abs(b) < Math.abs(a)) {
            /* .. except when `b` is a smaller negative integer */
            f = b;
            b = a;
            a = f;
            intflag = true;
        }

        if ((Math.abs(a) > Math.abs(c) + 1 || intflag) && Math.abs(c - a) > 2
                && Math.abs(a) > 2) {
            /* |a| >> |c| implies that large cancellation error is to be expected.
             *
             * We try to reduce it with the recurrence relations
             */
            return hyp2f1ra(a, b, c, x, loss);
        }

        i = 0;
        umax = 0.0;
        f = a;
        g = b;
        h = c;
        s = 1.0;
        u = 1.0;
        k = 0.0;
        do {
            if (Math.abs(h) < EPS) {
	            loss = 1.0;
                return Double.POSITIVE_INFINITY;
            }
            m = k + 1.0;
            u = u * ((f + k) * (g + k) * x / ((h + k) * m));
            s += u;
            k = Math.abs(u);		/* remember largest term summed */
            if (k > umax)
                umax = k;
            k = m;
            if (++i > MAX_ITERATIONS) {	/* should never happen */
	    loss = 1.0;
                return (s);
            }
        }
        while (s == 0 || Math.abs(u / s) > MACHEP);

        /* return estimated relative error */
    loss = (MACHEP * umax) / Math.abs(s) + (MACHEP * i);

        return (s);
    }


    /*
     * Evaluate hypergeometric function by two-term recurrence in `a`.
     *
     * This avoids some of the loss of precision in the strongly alternating
     * hypergeometric series, and can be used to reduce the `a` and `b` parameters
     * to smaller values.
     *
     * AMS55 #15.2.10
     */
    static double hyp2f1ra(double a, double b, double c, double x,
                           Double loss)
    {
        double f2, f1, f0;
        int n;
        double t,  da;
        Double err = 0.0;

        /* Don't cross c or zero */
        if ((c < 0 && a <= c) || (c >= 0 && a >= c)) {
            da = Math.round(a - c);
        }
        else {
            da = Math.round(a);
        }
        t = a - da;

        loss = 0.0;

        assert(da != 0);

        if (Math.abs(da) > MAX_ITERATIONS) {
            /* Too expensive to compute this value, so give up */
            System.out.println("hyp2f1 : no result ");
            loss = 1.0;
            return Double.NaN;
        }

        if (da < 0) {
            /* Recurse down */
            f2 = 0;
            f1 = hys2f1(t, b, c, x, err);
	    loss += err;
            f0 = hys2f1(t - 1, b, c, x, err);
	    loss += err;
            t -= 1;
            for (n = 1; n < -da; ++n) {
                f2 = f1;
                f1 = f0;
                f0 = -(2 * t - c - t * x + b * x) / (c - t) * f1 - t * (x -
                        1) /
                        (c - t) * f2;
                t -= 1;
            }
        }
        else {
            /* Recurse up */
            f2 = 0;
            f1 = hys2f1(t, b, c, x, err);
	loss += err;
            f0 = hys2f1(t + 1, b, c, x, err);
	loss += err;
            t += 1;
            for (n = 1; n < da; ++n) {
                f2 = f1;
                f1 = f0;
                f0 = -((2 * t - c - t * x + b * x) * f1 +
                        (c - t) * f2) / (t * (x - 1));
                t += 1;
            }
        }

        return f0;
    }


    /*
        15.4.2 Abramowitz & Stegun.
    */
    static double hyp2f1_neg_c_equal_bc(double a, double b, double x)
    {
        double k;
        double collector = 1;
        double sum = 1;
        double collector_max = 1;

        if (!(Math.abs(b) < 1e5)) {
            return Double.NaN;
        }

        for (k = 1; k <= -b; k++) {
            collector *= (a + k - 1)*x/k;
            collector_max = Math.max(Math.abs(collector), collector_max);
            sum += collector;
        }

        if (1e-16 * (1 + collector_max/Math.abs(sum)) > 1e-7) {
            return Double.NaN;
        }

        return sum;
    }
}
