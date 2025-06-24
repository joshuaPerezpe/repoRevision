function fn() {
    let iniYellow = '\u001b[33;1m'; //YellowBold
    let iniGreen = '\u001b[92;1m'; //GreenBold
    let finR = '\u001b[0m';

    try {
        karate.configure('ssl', { trustAll: true, keyStore: 'classpath:integration/cert/qaApigeeCertificate.p12', keyStorePassword: 'Bice2023', keyStoreType: 'pkcs12' });
    } catch (error) {
        karate.log(iniYellow+'WARN: Error setting keyStore' + finR,iniGreen + error + finR);
    }
    var env = karate.env;
    let DProvider = karate.properties['karate.DProvider']??true;

    if (env != null) {
        env = karate.env.toLowerCase();
    }

    if (!env || (env != 'qa' && env != 'dev' && env != 'prod')) {
        env = 'qa';
    }

    karate.log(iniYellow + 'WARN: karate.env system property set:' + finR, iniGreen + env + finR);
    karate.log(iniYellow + 'WARN: karate.DProvider system property set call Data Provider:' + finR, iniGreen + DProvider + finR);

    let nameEnvFile = env === 'perf' ? 'env_qa.json' : `env_${env}.json`;
    let nameReadEnvFile = karate.read(`classpath:integration/env/${nameEnvFile}`);

    if (!karate.tags.includes('env=perf') && DProvider !=  'false') {
        try {
            const idProject = nameReadEnvFile.data_provider_id;
            if (idProject === null || idProject === undefined || typeof idProject !== 'string') {
                karate.log(iniYellow+'Favor revisar que la variable {data_provider_id}, se encuentre presente en el archivo: '+finR, nameEnvFile);
            } else {
                const result = karate.callSingle('classpath:data-provider.js', idProject);

                if (result != null ) {
                    const data = result.data;
                    const scenario_name = karate.scenario.name;
                    const tag = scenario_name.split('_')[0].trim();

                    let data_schema;
                    if (/^\d+$/.test(tag)) {
                        data_schema = data.find(x => x.idAzure === tag);
                    } else {
                        data_schema = data.find(x => x.idTestJira === tag);
                    }

                    if (data_schema === undefined) {
                        karate.log(iniYellow + "EL TESTCASE " + finR + iniGreen + tag + finR + iniYellow +" NO POSEE DATOS GUARDADOS EN DATA PROVIDER" + finR);
                    }

                    karate.set("data", data_schema);
                } else {
                    karate.log(iniYellow + 'El {data_provider_id} -> '+ idProject +' proporcionado, no posee datos en el data provider.' + finR);
                }
            }
        } catch (e) {
            karate.fail('Ha ocurrido una excepción con Data Provider: ' + e.message);
        }
    }

    return nameReadEnvFile;
}