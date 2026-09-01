public record MyFunction(int functionId, String functionDefinition, boolean isConstant) {

    @Override
    public String toString() {
        return  String.format("{id=%d} | f(x) = %s", functionId, functionDefinition);
    }
}
