// Pong — Programmazione Orientata agli Oggetti e Laboratori, Udine, a.a. 2026-27
// Consegna 1: il Punteggio. Scadenza: domenica 25 ottobre 2026.
package pong;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * I conti di una partita: chi ha segnato, quanti punti ha ciascun lato, chi ha vinto.
 * La Partita gli dice chi ha fatto punto; lo Schermo chiede i punti alla Partita, che li chiede a lui.
 *
 * La regola per vincere: un lato vince quando ha almeno {@code puntiPerVincere} punti e almeno
 * {@code scartoMinimo} punti più dell'altro. Esempi, con 11 punti e scarto 2: 11 a 9 vince;
 * 11 a 10 si continua; 12 a 10 vince. Con scarto 1 vince chi arriva per primo a 11.
 *
 * IL PEZZO MANCANTE: i corpi dei metodi qui sotto (e i campi che vi servono).
 * Ogni metodo ha il suo contratto, nel commento sopra: cosa pretende, cosa promette.
 * I test ufficiali (cartella test) controllano i contratti.
 */


public class Punteggio {
    private final int puntiPerVincere;
    private final int scartoMinimo;

    private final int[] punteggio;
    private Lato vincitore;

    private final List<Lato> storia;

    /**
     * INVARIANTE:
     * 1. puntiPerVincere e scartoMinimo sempre >= 1.
     * 2. L'array punteggio e la lista storia non sono mai null.
     * 3. La lista storia non contiene mai elementi null.
     * 4. I punti nell'array punteggio corrispondono esattamente al numero di 
     *    occorrenze di ciascun lato nella lista storia.
     * 5. vincitore mai null se e solo se uno dei lati ha raggiunto
     *    i puntiPerVincere e scartoMinimo.
     * 6. A partita finita Storia non cambia.
     */

    boolean repOK() {

        if (this.puntiPerVincere < 1 || this.scartoMinimo < 1) return false;
        if (this.punteggio == null || this.storia == null) return false;
        if (this.punteggio.length != Lato.values().length) return false;

        int[] puntiRicalcolati = new int[Lato.values().length];
        Lato vincitoreCalcolato = null;

        for (Lato l : this.storia) {
            if (l == null) return false; 
            

            if (vincitoreCalcolato != null) {
                return false;
            }

            puntiRicalcolati[l.ordinal()]++; 

            for (Lato lato : Lato.values()) {
                int p = puntiRicalcolati[lato.ordinal()];
                int hig = 0;
                for (Lato nlato : Lato.values()) {
                    if (lato != nlato && puntiRicalcolati[nlato.ordinal()] > hig) {
                        hig = puntiRicalcolati[nlato.ordinal()];
                    }
                }

                if (p >= this.puntiPerVincere && (p - hig) >= this.scartoMinimo) {
                    vincitoreCalcolato = lato;
                }
            }
        }
        for (Lato l : Lato.values()) {
            if (this.punteggio[l.ordinal()] != puntiRicalcolati[l.ordinal()]) {
                return false;
            }
        }
        if (this.vincitore != vincitoreCalcolato) return false;

        return true;
    }

    /**
     * **costruttore** `Punteggio(int puntiPerVincere, int scartoMinimo)` 
     * Pretende: niente. 
     * Promette: una partita 0 a 0, non finita, senza vincitore e con la storia vuota. 
     * Lancia `IllegalArgumentException` se
     * `puntiPerVincere` è minore di 1 o `scartoMinimo` è minore di 1.
     */
    public Punteggio(int puntiPerVincere, int scartoMinimo) {
        if (puntiPerVincere < 1) {
            throw new IllegalArgumentException("puntiPerVincere deve essere almeno 1");
        }
        if (scartoMinimo < 1) {
            throw new IllegalArgumentException("scartoMinimo deve essere almeno 1");
        }
        
        this.punteggio = new int[Lato.values().length];
        this.vincitore = null;

        this.puntiPerVincere = puntiPerVincere;
        this.scartoMinimo = scartoMinimo;

        this.storia = new ArrayList<>();
    }

    private void controllaVincitore() {
        this.vincitore = null;
        for (Lato lato : Lato.values()) {
            if (this.punteggio[lato.ordinal()] >= this.puntiPerVincere) {
                int hig = 0;
                for (Lato nlato : Lato.values()) {
                    if (lato == nlato) {
                        continue;
                    }

                    if (this.punteggio[nlato.ordinal()] > hig) {
                        hig = this.punteggio[nlato.ordinal()];
                    }
                }

                if (this.punteggio[lato.ordinal()] - hig < this.scartoMinimo) {
                    continue;
                } else {
                    this.vincitore = lato;
                    return;
                }
            }
        }
    }

    /**
     * Il lato chi ha fatto un punto.
     * Pretende: niente. Promette: come prima. Se `chi` è null lancia `IllegalArgumentException`, anche a partita finita, e non cambia niente.
     * Promette: se la partita non era finita, il lato chi ha un punto in più, l'altro lato non
     *           cambia e il punto si aggiunge in fondo alla storia; se con questo punto il lato chi
     *           vince (regola in cima alla classe), la partita è finita e il vincitore è chi.
     *           Se la partita era già finita, non cambia niente.
     */
    public void segnaPunto(Lato chi) {
        if (chi == null) {
            throw new IllegalArgumentException("il lato non deve essere null");
        }

        if (this.isFinita()) {
            return;
        }

        this.punteggio[chi.ordinal()]++;
        this.storia.add(chi);

        controllaVincitore();
    }

    /**
     * Pretende: niente. Promette: come prima. Lancia `IllegalArgumentException` se `chi` è null.
     * Promette: i punti del lato chi. Non cambia niente.
     */
    public int getPunti(Lato chi) {
        if (chi == null) {
            throw new IllegalArgumentException("il lato non deve essere null");
        }

        return this.punteggio[chi.ordinal()];
    }

    /**
     * Promette: vero se la partita è finita, cioè se un lato ha vinto. Non cambia niente.
     */
    public boolean isFinita() {
        return this.vincitore != null;
    }

    /**
     * Promette: il lato che ha vinto; null se la partita non è finita. Non cambia niente.
     */
    public Lato getVincitore() {
        return this.vincitore;
    }

    /**
     * L'arbitro ci ripensa: toglie l'ultimo punto della storia (l'ultimo segnato e non già annullato).
     * Pretende: niente.
     * Promette: se nella storia c'è almeno un punto, l'ultimo punto viene tolto, dai conti e dalla
     *           storia, come se non fosse mai stato segnato. Se era il punto che aveva chiuso la
     *           partita, la partita torna in corso, senza vincitore. Se la storia è vuota, non cambia
     *           niente.
     */
    public void annullaUltimoPunto() {
        if (this.storia.isEmpty()) {
            return;
        }

        Lato lscor = this.storia.removeLast();

        this.punteggio[lscor.ordinal()]--;

        controllaVincitore();
    }

    /**
     * La storia dei punti, dal primo all'ultimo: per ogni punto segnato (e non annullato), il lato
     * che l'ha segnato.
     * Pretende: niente.
     * Promette: una lista con i punti in ordine. Chi riceve la lista non può cambiare il Punteggio
     *           attraverso di lei: aggiungere, togliere o cambiare elementi della lista ricevuta
     *           (se la lista lo permette) non cambia i punti, il vincitore né la storia del Punteggio.
     *           Non promette altro: la lista ricevuta può restare com'era o mostrare anche i punti
     *           segnati dopo; chi la usa non deve contare né sull'una né sull'altra cosa.
     */
    public List<Lato> getStoria() {
        return new ArrayList<>(this.storia);
    }

    /**
     * Una partita nuova con la stessa regola.
     * Pretende: niente.
     * Promette: 0 a 0, non finita, senza vincitore, storia vuota; la regola per vincere non cambia.
     */
    public void azzera() {
        Arrays.fill(this.punteggio, 0);
        this.vincitore = null;
        this.storia.clear();
    }
}
