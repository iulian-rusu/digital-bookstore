import React, {Component} from 'react'

export default class PastOrder extends Component {
    render() {
        const orderedItems = this.props.item.items
        .map(i => <p><b>{i.isbn}</b> x {i.quantity} (${i.quantity * i.price})</p>)
        return (
            <tr>
                <td>{this.props.item.orderId}</td>
                <td>{this.props.item.date}</td>
                <td>{orderedItems}</td>
                <td>{this.props.item.status}</td>
            </tr>
        )
    }
}
