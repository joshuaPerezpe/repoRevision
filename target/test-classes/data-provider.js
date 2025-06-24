function fn(idProject) {
    const DataProviders = Java.type('integration.helpers.DP.DataProviders');
    const getDataProver = DataProviders.getFromDP(idProject);
    if(getDataProver!=null){
        return { data: JSON.parse(getDataProver) };
    }
    return null
}
