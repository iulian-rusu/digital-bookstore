import React, { Component } from 'react'

export default class PastOrder extends Component {
    render() {
        return (
            <tr>
                <td>{this.props.item.id}</td>
                <td>{this.props.item.date}</td>
                <td>{this.props.item.isbnList}</td>
                <td>{this.props.item.status}</td>
            </tr>
        )
    }
}
