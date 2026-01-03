package com.github.jelmerk.knn.examples.my.art4d_cel;
import com.github.jelmerk.knn.examples.my.art4d_cel.myart_cel.LocalState;
/**
 * ��ȫ��Բ���ָ������� CEL(qqc, pp, aa, bb)
 */
public class EllipticIntegralCEL {

    // ���峣��
    private static final double CA = 0.0003;
    private static final double PIO2 = 1.57079632679490;

    // ״̬����
    private static int state = 0;

    /**
     * ��ȫ��Բ������غ��� CEL(qqc, pp, aa, bb)
     *
     * @param qqc ģ��
     * @param pp ���� p
     * @param aa ϵ�� a
     * @param bb ϵ�� b
     * @return ������
     */
    public static double cel(double qqc, double pp, double aa, double bb) {
        state = 0; // ��ʼ��״̬

        if (qqc == 0.0) {
            state = -1;
            throw new IllegalArgumentException("Bad qqc in routine CEL");
        }

        double a = aa;
        double b = bb;
        double p = pp;
        double e = qqc;
        double em = 1.0;
        double q, f, g;

        double qc = Math.abs(qqc);

        try {
            if (p > 0.0) {
                p = Math.sqrt(p);
                b /= p;
            } else {
                f = qc * qc;
                q = 1.0 - f;
                g = 1.0 - p;
                f -= p;
                q *= (b - a * p);
                p = Math.sqrt(f / g);
                a = (a - b) / g;
                b = -q / (g * g * p) + a * p;
            }

            for (;;) {
                double fOld = a;
                a += (b / p);
                g = e / p;
                b += (fOld * g);
                b += b;
                p = g + p;
                g = em;
                em += qc;

                if (Math.abs(g - qc) <= g * CA) {
                    break;
                }

                qc = Math.sqrt(e);
                qc += qc;
                e = qc * em;
            }

            state = 1;
            return PIO2 * (b + a * em) / (em * (em + p));
        } catch (Exception ex) {
            state = -2;
            throw new RuntimeException("Error during calculation in CEL", ex);
        }
    }

    /**
     * ��װ�ӿڣ����� cel �����ط�װ��״̬����
     */
    public static LocalState resultWithState(double qqc, double pp, double aa, double bb) {
        try {
            cel(qqc, pp, aa, bb); // ʵ�ʵ��ú��ķ���
        } catch (Exception e) {
            // �쳣ʱ�����ú� state
        }
        return new LocalState(state);
    }

    /**
     * ����������
     */
//    public static void main(String[] args) {
//        double qqc = 0.5;   // ģ��
//        double pp = 0.2;    // ���� p
//        double aa = 1.0;    // ϵ�� a
//        double bb = 0.8;    // ϵ�� b
//
//        System.out.printf("cel(%.4f, %.4f, %.4f, %.4f) = %.12f%n",
//                qqc, pp, aa, bb, cel(qqc, pp, aa, bb));
//        System.out.println("��ǰ״̬: " + resultWithState(qqc, pp, aa, bb).getValue());
//    }
}
