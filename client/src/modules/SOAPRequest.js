function wrap(msg) {
    return `<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
        xmlns:gs="http://identity.pos.com">
        <soapenv:Header/>
        <soapenv:Body>
           ${msg}
        </soapenv:Body>
    </soapenv:Envelope>`
}


export async function postAuth(username, password) {
    const authRequest = `
    <gs:userAuthenticationRequest>
        <gs:username>${username}</gs:username>
        <gs:password>${password}</gs:password>
    </gs:userAuthenticationRequest>
    `
    const requestBody = wrap(authRequest)

    const options = {
        method: 'post',
        headers: {
            'Content-Type': 'text/xml'
        },
        body: requestBody
    }
    const response =  await fetch('http://localhost:8082/api', options)
    return response
}

export async function postUpdateUser(userId, newPassword, token) {
    const updateRequest = `
    <gs:userUpdateRequest>
        <gs:token>${token}</gs:token>
        <gs:userId>${userId}</gs:userId>
        <gs:password>${newPassword}</gs:password>
    </gs:userUpdateRequest>
    `
    const requestBody = wrap(authRequest)

    const options = {
        method: 'post',
        headers: {
            'Content-Type': 'text/xml'
        },
        body: requestBody
    }
    const response =  await fetch('http://localhost:8082/api', options)
    return response
}

export function extractUserData(authResponse) {
    const parser = new DOMParser()
    const xml = parser.parseFromString(authResponse.text(), 'text/xml')
    const token = xml.getElementsByTagNameNS('ns2', 'token')[0].textContent
    const userId = xml.getElementsByTagNameNS('ns2', 'sub')[0].textContent
    const role = xml.getElementsByTagNameNS('ns2', 'role')[0].textContent
    return {
        token: token,
        userId: userId,
        role: role
    }
}