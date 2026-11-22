package ru.job4j.cash;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AccountStorageTest {

    @Test
    void whenAdd() {
        var storage = new AccountStorage();
        storage.add(new Account(1, 100));
        var firstAccount = storage.getById(1)
                .orElseThrow(() -> new IllegalStateException("Not found account by id = 1"));
        assertThat(firstAccount.amount()).isEqualTo(100);
    }

    @Test
    void whenUpdate() {
        var storage = new AccountStorage();
        storage.add(new Account(1, 100));
        storage.update(new Account(1, 200));
        var firstAccount = storage.getById(1)
                .orElseThrow(() -> new IllegalStateException("Not found account by id = 1"));
        assertThat(firstAccount.amount()).isEqualTo(200);
    }

    @Test
    void whenDelete() {
        var storage = new AccountStorage();
        storage.add(new Account(1, 100));
        storage.delete(1);
        assertThat(storage.getById(1)).isEmpty();
    }

    @Test
    void whenTransfer() {
        var storage = new AccountStorage();
        storage.add(new Account(1, 100));
        storage.add(new Account(2, 100));
        storage.transfer(1, 2, 100);
        var firstAccount = storage.getById(1)
                .orElseThrow(() -> new IllegalStateException("Not found account by id = 1"));
        var secondAccount = storage.getById(2)
                .orElseThrow(() -> new IllegalStateException("Not found account by id = 1"));
        assertThat(firstAccount.amount()).isEqualTo(0);
        assertThat(secondAccount.amount()).isEqualTo(200);
    }

    @Test
    void whenTransferThenReturnTrue() {
        var storage = new AccountStorage();
        storage.add(new Account(1, 100));
        storage.add(new Account(2, 100));
        assertThat(storage.transfer(1, 2, 50)).isTrue();
    }

    @Test
    void whenTransferMoreThanAmountThenReturnFalseAndBalanceUnchanged() {
        var storage = new AccountStorage();
        storage.add(new Account(1, 100));
        storage.add(new Account(2, 100));
        assertThat(storage.transfer(1, 2, 150)).isFalse();
        assertThat(storage.getById(1).orElseThrow().amount()).isEqualTo(100);
        assertThat(storage.getById(2).orElseThrow().amount()).isEqualTo(100);
    }

    @Test
    void whenTransferFromNotExistsThenReturnFalse() {
        var storage = new AccountStorage();
        storage.add(new Account(2, 100));
        assertThat(storage.transfer(1, 2, 50)).isFalse();
        assertThat(storage.getById(2).orElseThrow().amount()).isEqualTo(100);
    }

    @Test
    void whenTransferToNotExistsThenReturnFalse() {
        var storage = new AccountStorage();
        storage.add(new Account(1, 100));
        assertThat(storage.transfer(1, 2, 50)).isFalse();
        assertThat(storage.getById(1).orElseThrow().amount()).isEqualTo(100);
    }

    @Test
    void whenTransferZeroThenReturnTrue() {
        var storage = new AccountStorage();
        storage.add(new Account(1, 100));
        storage.add(new Account(2, 100));
        assertThat(storage.transfer(1, 2, 0)).isTrue();
        assertThat(storage.getById(1).orElseThrow().amount()).isEqualTo(100);
        assertThat(storage.getById(2).orElseThrow().amount()).isEqualTo(100);
    }

    @Test
    void whenTransferBetweenSameAccountThenReturnFalseAndBalanceUnchanged() {
        var storage = new AccountStorage();
        storage.add(new Account(1, 100));
        assertThat(storage.transfer(1, 1, 50)).isFalse();
        assertThat(storage.getById(1).orElseThrow().amount()).isEqualTo(100);
    }
}
