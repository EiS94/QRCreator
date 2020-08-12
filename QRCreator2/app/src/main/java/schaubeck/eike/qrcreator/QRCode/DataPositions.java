package schaubeck.eike.qrcreator.QRCode;



public class DataPositions {

    ReservedModulesMask mask;
    int i;
    int j;
    boolean call = true;

    public DataPositions(ReservedModulesMask mask) {
        this.mask = mask;
        i = mask.size() - 1;
        j = mask.size() - 1;
    }

    public int i() {
        return i;
    }

    public int j() {
        return j;
    }


    public boolean next() {
        boolean foundNext = false;
        if (call) {
            while (!foundNext || (i > 0 && j > 0)) {

                if (i == 9 && j == 7) {
                    j = j - 2;
                    foundNext = true;
                    break;
                }

                else if (j > 6) {
                    if (j % 2 == 0) {
                        j--;
                        if (!mask.isReserved(i, j)) {
                            foundNext = true;
                            break;
                        }
                    } else if ((j + 1) % 4 == 0) {
                        if (i > 0) {
                            j++;
                            i--;
                            if (!mask.isReserved(i, j)) {
                                foundNext = true;
                                break;
                            }
                        } else {
                            j--;
                            if (!mask.isReserved(i, j)) {
                                foundNext = true;
                                break;
                            }
                        }
                    } else if ((j + 1) % 2 == 0) {
                        if (i < mask.size() - 1) {
                            j++;
                            i++;
                            if (!mask.isReserved(i, j)) {
                                foundNext = true;
                                break;
                            }
                        } else {
                            j--;
                            if (!mask.isReserved(i, j)) {
                                foundNext = true;
                                break;
                            }
                        }
                    }
                }

                else {
                    if (j % 2 == 1) {
                        j--;
                        if (j >= 0 && i >= 0) {
                            if (!mask.isReserved(i, j)) {
                                foundNext = true;
                                break;
                            }
                        } else break;
                    } else if (j % 4 == 0) {
                        if (i < mask.size() - 1) {
                            j++;
                            i++;
                            if (j >= 0 && i >= 0) {
                                if (!mask.isReserved(i, j)) {
                                    foundNext = true;
                                    break;
                                }
                            } else break;
                        } else {
                            j--;
                            if (j >= 0 && i >= 0) {
                                if (!mask.isReserved(i, j)) {
                                    foundNext = true;
                                    break;
                                }
                            } else break;
                        }
                    } else if (j % 2 == 0) {
                        if (i > 0) {
                            j++;
                            i--;
                            if (j >= 0 && i >= 0) {
                                if (!mask.isReserved(i, j)) {
                                    foundNext = true;
                                    break;
                                }
                            } else break;
                        } else {
                            j--;
                            if (j >= 0 && i >= 0) {
                                if (!mask.isReserved(i, j)) {
                                    foundNext = true;
                                    break;
                                }
                            } else break;
                        }
                    }
                }
            }
        }
        if (!foundNext) call = false;
        return foundNext;
    }
}


