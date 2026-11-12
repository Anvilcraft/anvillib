package net.anvilcraft.anvillib.api.types;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public abstract class Result<T, E extends Exception> implements IReference<T> {

    public static <T, E extends Exception> Result<T, E> ok(T value) {
        return new Ok<>(value);
    }

    @SuppressWarnings("unchecked")
    public static <T, E extends Exception> Result<T, E> error(E error) {
        return (Result<T, E>) new Error<E>(error);
    }

    @SuppressWarnings("unchecked")
    public static <T, E extends Exception, I> Result<T, E> create(I input, IThrowingFunction<I, T, E> function) {
        try {
            T output = function.accept(input);
            return ok(output);
        } catch (Exception e) {
            return error((E) e);
        }
    }

    public abstract T getOrNull();

    public abstract T getOrThrow() throws E;

    public abstract E getError();

    public abstract void throwError() throws E;

    public abstract <O> Result<O, E> map(IThrowingFunction<T, O, E> function);

    public abstract <O> Result<O, E> flatMap(Function<T, Result<O, E>> function);

    public abstract <F extends Exception> Result<T, F> mapError(Function<E, F> function);

    public abstract boolean isError();

    public abstract Optional<T> toOptional();

    public abstract Stream<T> toStream();

    private static class Error<E extends Exception> extends Result<Object, E> {

        private E error;

        private Error(E error) {
            this.error = error;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <O> Result<O, E> map(IThrowingFunction<Object, O, E> function) {
            return (Result<O, E>) this;
        }

        @Override
        public Object getOrNull() {
            return null;
        }

        @Override
        public E getError() {
            return this.error;
        }

        @Override
        public void throwError() throws E {
            throw this.error;
        }

        @Override
        public boolean isNull() {
            return true;
        }

        @Override
        public Object get() {
            throw new IllegalStateException(this.error);
        }

        @SuppressWarnings("unchecked")
        @Override
        public <O> Result<O, E> flatMap(Function<Object, Result<O, E>> function) {
            return (Result<O, E>) this;
        }

        @Override
        public Object getOrThrow() throws E {
            throw this.error;
        }

        @Override
        public <F extends Exception> Result<Object, F> mapError(Function<E, F> function) {
            return new Error<F>(function.apply(error));
        }

        @Override
        public boolean isError() {
            return true;
        }

        @Override
        public Optional<Object> toOptional() {
            return Optional.empty();
        }

        @Override
        public Stream<Object> toStream() {
            return Stream.empty();
        }
    
    }

    private static class Ok<T, E extends Exception> extends Result<T, E>  {

        private T value;

        private Ok(T value) {
            this.value = value;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <O> Result<O, E> map(IThrowingFunction<T, O, E> function) {
            try {
                O result = function.accept(this.value);
                return new Ok<>(result);
            } catch (Exception e) {
                return (Result<O, E>) new Error<E>((E) e);
            }
        }

        @Override
        public T getOrNull() {
            return this.value;
        }

        @Override
        public E getError() {
            return null;
        }

        @Override
        public void throwError() throws E {
            
        }

        @Override
        public boolean isNull() {
            return this.value == null;
        }

        @Override
        public T get() {
            if (this.value != null) return this.value;
            throw new NullPointerException();
        }

        @Override
        public <O> Result<O, E> flatMap(Function<T, Result<O, E>> function) {
            return function.apply(this.value);
        }

        @Override
        public T getOrThrow() throws E {
            return this.get();
        }

        @SuppressWarnings("unchecked")
        @Override
        public <F extends Exception> Result<T, F> mapError(Function<E, F> function) {
            return (Result<T, F>) this;
        }

        @Override
        public boolean isError() {
            return false;
        }

        @Override
        public Optional<T> toOptional() {
            return Optional.ofNullable(this.value);
        }

        @Override
        public Stream<T> toStream() {
            return Stream.of(this.value);
        }
        
    }

}
